import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default {
    state: {
        authenticated: null,
        visibility: false,
    },
    mutations: {
        authenticate(state, auth) {
            if (auth.username === 'admin' && auth.password === 'password') {
                this.state.authenticated = true
            } else {
                this.state.authenticated = false
            }
        },
        // gets the attribute visibility from QuestionCreation
        changeVisible(state, value) {
            this.state.visibility = value
        },
    },
    getters: {
        // hier liegt ein Fehler...
        isAuthenticated: (state) => {
            return state.authenticated
        },
        isVisible: (state) => {
            return state.visibility
        },
    },
}
