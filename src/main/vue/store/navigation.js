export const state = () => ({
    Polls: [],
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
        let error = ''
        const data = await this.$axios.get('/poll').catch((reason) => {
            console.log(reason)
            error = reason
        })
        if (error.length === 0) {
            console.log('alles gut')
            commit('setPolls', data)
        } else {
            console.log('nix gut')
            commit('saveError', error)
        }
    },
}
