import axios from 'axios'
import auth from '../api/auth'

export const state = () => ({
    authenticated: false,
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
            this.state.token = token
            this.state.authenticated = true
            axios.defaults.headers.Authorization = token
        } else {
            this.state.authenticated = false
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
            auth.login(input.username, input.password)
                .then((res) => {
                    const token = res.headers.authorization
                    commit('authenticate', token)
                    resolve()
                })
                .catch(() => {
                    commit('authenticate', null)
                    reject(new Error('Authentification failed'))
                })
        })
    },
}

/*
store.subscribe((mutation, state) => {
    localStorage.setItem('store', JSON.stringify(state))
    axiosInstance.defaults.headers.Authorization = state.token
})
*/
