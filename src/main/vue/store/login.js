import axios from 'axios'

const store = new Vuex.Store({
    state: {
        authenticated: null,
    },
    mutations: {
        authenticate(state, token) {
            if (token !== null) {
                this.state.token = token
                this.state.authenticated = true
                axios.defaults.headers['Authorization'] = token
            } else {
                this.state.authenticated = false
            }
        },
        initializeStore(state) {
            if (localStorage.getItem('store')) {
                this.replaceState(Object.assign(state, JSON.parse(localStorage.getItem('store'))))
            }
        },
    },
    actions: {
        requestToken({ commit }, credentials) {
            return new Promise((resolve, reject) => {
                api.auth
                    .login(credentials.username, credentials.password)
                    .then((res) => {
                        let token = res.headers.authorization
                        commit('authenticate', token)
                        resolve()
                    })
                    .catch(() => {
                        commit('authenticate', null)
                        reject()
                    })
            })
        },
    },
    getters: {

    },

})

store.subscribe((mutation, state) => {
    localStorage.setItem('store', JSON.stringify(state))
    axios.defaults.headers['Authorization'] = state.token
})

export default store
