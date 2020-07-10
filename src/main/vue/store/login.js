export const state = () => ({
    authenticated: null,
    token: '',
    username: '',
})

export const getters = {
    getToken(state) {
        if (state !== undefined) {
            return state.token
        }
        return null
    },
    isAuthenticated(state) {
        return state.authenticated
    },
    getUsername(state) {
        return state.username
    },
}

export const mutations = {
    authenticate(state, token) {
        if (token !== null) {
            state.token = token
            state.authenticated = true
            localStorage.setItem('user-token', token)
            const today = new Date()
            const tokenString = localStorage.getItem('user-token')
            this.$axios.defaults.baseURL = 'http://127.0.0.1:8088/api'
            this.$axios.defaults.headers.common = {
                Authorization: 'Bearer ' + tokenString,
                username: state.username,
                timeZoneOffset: -today.getTimezoneOffset(),
            }
            this.$axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded'
        } else {
            state.authenticated = false
        }
    },
    setUsername(state, username) {
        state.username = username
        localStorage.setItem('user-name', username)
    },
    logout(state) {
        console.log('logout')
        state.authenticated = false
        localStorage.setItem('user-token', '')
        mutations.setUsername(state, '')
        const today = new Date()
        this.$axios.defaults.headers.common = {
            Authorization: 'Bearer ',
            username: '',
            timeZoneOffset: -today.getTimezoneOffset(),
        }
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
    async autoLogin({ state, commit }) {
        const token = localStorage.getItem('user-token')
        const tmpUsername = localStorage.getItem('user-name')
        if (token.length > 0) {
            const today = new Date()
            this.$axios.defaults.baseURL = 'http://127.0.0.1:8088/api'
            this.$axios.defaults.headers.common = {
                Authorization: 'Bearer ' + token,
                username: tmpUsername,
                timeZoneOffset: -today.getTimezoneOffset(),
            }
            this.$axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded'
        }
        await this.$axios
            .get('/checkAutologin')
            .catch((error) => {
                console.log(error)
                commit('authenticate', null)
            })
            .then((response) => {
                if (response.status === 200) {
                    console.log(response)
                    commit('setUsername', tmpUsername)
                    commit('authenticate', token)
                    this.$router.push('/')
                } else {
                    commit('authenticate', null)
                }
            })
    },
}
