import axios from 'axios'

export const state = () => ({
    authenticated: null,
    token: '',
    username: '',
})

export const getters = {
    isAuthenticated: (state) => {
        return state.authenticated
    },
    getUsername: (state) => {
        return state.username
    },
}

export const mutations = {
    authenticate(state, token) {
        console.log(token)
        if (token !== null) {
            state.token = token
            state.authenticated = true
            axios.defaults.headers.Authorization = token
        } else {
            state.authenticated = false
        }
    },
    setUsername(state, username) {
        state.username = username
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
            axios.defaults.headers.Authorization = state.token
            axios
                .post('http://localhost:8088/token/generate-token', credentials)
                .then((res) => {
                    const token = res.data.result.token
                    commit('authenticate', token)
                    commit('setUsername', input.username)
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
