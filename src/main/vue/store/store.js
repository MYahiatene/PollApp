import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default {
    state: {
        authenticated: null,
    },
    mutations: {
        authenticate(state, auth) {
            if (auth.username === 'admin' && auth.password === 'password') {
                this.state.authenticated = true
            } else {
                this.state.authenticated = false
            }
        },
    },
    getters: {
        isAuthenticated: (state) => {
            return state.authenticated
        },
    },
}
