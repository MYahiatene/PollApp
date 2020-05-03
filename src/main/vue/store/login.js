export const state = () => {}
export const mutations = {
    authenticate(state, auth) {
        if (auth.username === 'admin' && auth.password === 'password') {
            this.state.authenticated = true
        } else {
            this.state.authenticated = false
        }
    },
}
export const getters = {
    // hier liegt ein Fehler...
    isAuthenticated: (state) => {
        return state.authenticated
    },
}
