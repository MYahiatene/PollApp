export const state = () => ({
    authenticated: null,
    token: '',
    username: '',
})

export const getters = {
    getToken: (state) => {
        if (state !== undefined) {
            return state.token
        }
        return null
    },
    isAuthenticated: (state) => {
        return state.authenticated
    },
    getUsername: (state) => {
        return state.username
    },
}

export const mutations = {
    authenticate(state, token) {
        if (token !== null) {
            state.token = token
            state.authenticated = true
            localStorage.setItem('user-token', token)
            const tokenString = localStorage.getItem('user-token')
            this.$axios.defaults.baseURL = 'http://127.0.0.1:8088/api'
            this.$axios.defaults.headers.common = {
                Authorization: 'Bearer ' + tokenString,
            }
            this.$axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded'
        } else {
            state.authenticated = false
        }
    },
    setUsername(state, username) {
        state.username = username
    },
}
export const actions = {
    async requestToken({ commit }, input) {
        const credentials = new URLSearchParams()
        credentials.append('username', input.username)
        credentials.append('password', input.password)
        const response = await this.$axios.post('http://127.0.0.1:8088/api/authenticate', credentials).catch()
        const token = response.data
        commit('authenticate', token)
        commit('setUsername', input.username)
    },
}
