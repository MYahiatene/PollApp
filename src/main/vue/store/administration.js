export const state = () => ({
    token: '',
    response: '',
})

export const getters = {
    getToken(state) {
        return state.token
    },
    getResponse(state) {
        return state.response
    },
}

export const mutations = {
    setTok(state) {
        state.token = localStorage.getItem('user-token')
    },
}
export const actions = {
    setToken({ commit }) {
        commit('setTok')
    },
}
