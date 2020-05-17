import axios from 'axios'

export const state = () => ({
    authenticated: null,
    token: '',
})

export const getters = {
    isAuthenticated: (state) => {
        return state.authenticated
    },
}

export const mutations = {
    authenticate(state, token) {
        if (token !== null) {
            state.token = token
            state.authenticated = true
            axios.defaults.headers.Authorization = token
        } else {
            state.authenticated = false
        }
    },
    /* initializeStore(state) {
        if (localStorage.getItem('store')) {
            this.replaceState(Object.assign(state, JSON.parse(localStorage.getItem('store'))))
        }
    }, */
}
export const actions = {
    requestToken({ commit }, input) {
        return new Promise((resolve, reject) => {
            const credentials = new URLSearchParams()
            credentials.append('username', input.username)
            credentials.append('password', input.password)
            axios
                .post('http://localhost:8088/token/generate-token', credentials)
                .then((res) => {
                    const token = res.data.result.token
                    commit('authenticate', token)
                    resolve()
                })
                .catch(() => {
                    commit('authenticate', null)
                    // reject(new Error('Authentification failed'))
                })
        })
    },
}

/* store.subscribe((mutation, state) => {
    localStorage.setItem('store', JSON.stringify(state))
    axios.defaults.headers.Authorization = state.token
}) */
