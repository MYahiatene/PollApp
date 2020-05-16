// import api from '../api/login'
// import axios from 'axios'

export const state = () => ({
    authenticated: false,
    token: '',
})
const axios = require('axios')
const instance = axios.create({
    baseURL: 'http://127.0.0.1:8088/api/',
    timeout: 1000,
    headers: { 'X-Custom-Header': 'foobar' },
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
    /*    initializeStore(state) {
        if (localStorage.getItem('store')) {
            this.replaceState(Object.assign(state, JSON.parse(localStorage.getItem('store'))))
        }
    }, */
}
export const actions = {
    requestToken({ commit }, credentials) {
        return new Promise((resolve, reject) => {
            const credentials = new URLSearchParams()
            credentials.append('username', credentials.username)
            credentials.append('password', credentials.password)
            instance
                .post('/api/authenticate', credentials)
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

/* store.subscribe((mutation, state) => {
    localStorage.setItem('store', JSON.stringify(state))
    axios.defaults.headers.Authorization = state.token
}) */
