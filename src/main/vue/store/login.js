import axios from 'axios'
import api from '../api/login'
// import Vuex from 'Vuex'

export const state = () => ({
    authenticated: null,
    token: null,
})
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
    initializeStore(state) {
        if (localStorage.getItem('store')) {
            this.replaceState(Object.assign(state, JSON.parse(localStorage.getItem('store'))))
        }
    },
}
export const actions = {
    requestToken({ commit }, credentials) {
        return new Promise((resolve, reject) => {
            api.login
                .login(credentials.username, credentials.password)
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

store.subscribe((mutation, state) => {
    localStorage.setItem('store', JSON.stringify(state))
    axios.defaults.headers.Authorization = state.token
})
