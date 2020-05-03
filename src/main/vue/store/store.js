import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const state = {
    authenticated: null,
}
const mutations = {
    authenticate(state, auth) {
        if (auth.username === 'admin' && auth.password === 'password') {
            this.state.authenticated = true
        } else {
            this.state.authenticated = false
        }
    },
}
const getters = {
    // hier liegt ein Fehler...
    isAuthenticated: (state) => {
        return state.authenticated
    },
}

export default new Vuex.Store({
    state,
    getters,
    mutations,
})
