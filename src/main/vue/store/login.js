import auth from '../api/auth'

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
            console.log(token)
            state.token = token
            state.authenticated = true
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
            auth.login(input.username, input.password)
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
