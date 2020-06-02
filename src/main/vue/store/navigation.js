export const state = () => ({
    Polls: [{}],
})
export const getters = {
    getPolls: (state) => {
        return state.Polls
    },
}
export const mutations = {
    setPolls: (state, data) => {
        state.Polls = data.data
    },
}
export const actions = {
    async initialize({ commit }) {
        const data = await this.$axios.get('/poll')
        commit('setPolls', data)
    },
}
