export const state = () => ({
    Polls: [],
    ParticipationLinks: [],
    error: '',
})
export const getters = {
    getPolls(state) {
        return state.Polls
    },
    getError(state) {
        return state.status
    },
}
export const mutations = {
    setPolls(state, data) {
        state.Polls = data.data
    },
    saveError(state, error) {
        state.error = error
    },
    setPollStatus(state, { pollId, data }) {
        for (let i = 0; i < state.Polls.length; i++) {
            if (state.Polls[i].pollId === pollId) {
                state.Polls[i].pollStatus = data.data
            }
        }
    },
}
export const actions = {
    async initialize({ commit, state }) {
        let error = ''
        const data = await this.$axios.get('/poll').catch((reason) => {
            console.log(reason)
            error = reason
        })
        if (error.length === 0) {
            commit('setPolls', data)
        } else {
            commit('saveError', error)
        }
    },
    async activatePoll({ commit }, pollId) {
        let error = ''
        const data = await this.$axios.post('/activatePoll/' + pollId).catch((reason) => {
            console.log(reason)
            error = reason
        })
        if (error.length === 0) {
            commit('setPollStatus', { pollId, data })
        } else {
            commit('saveError', error)
        }
    },
}
