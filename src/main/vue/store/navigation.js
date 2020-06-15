export const state = () => ({
    Polls: [{}],
    ParticipationLinks: [{}],
})
export const getters = {
    getPolls(state) {
        return state.Polls
    },
}
export const mutations = {
    setPolls(state, data) {
        state.Polls = data.data
    },
    setPollActive(state, pollId) {
        for (let i = 0; i < state.Polls.length; i++) {
            if (state.Polls[i].pollId === pollId) {
                state.Polls[i].pollStatus = 1
            }
        }
    },
    setPollFinished(state, pollId) {
        for (let i = 0; i < state.Polls.length; i++) {
            if (state.Polls[i].pollId === pollId) {
                state.Polls[i].pollStatus = 2
            }
        }
    },
}
export const actions = {
    async initialize({ commit }) {
        const data = await this.$axios.get('/poll')
        commit('setPolls', data)
    },
}
