export const state = () => ({
    Polls: [],
    error: '',
    index: -1,
    qrTitle: '',
    qrLink: '',
})
export const getters = {
    getPolls(state) {
        return state.Polls
    },
    getError(state) {
        return state.status
    },
    getPoll(state) {
        return state.Polls[state.index]
    },
    getQrTitle(state) {
        return state.qrTitle
    },
    getQrLink(state) {
        return state.qrLink
    },
}
export const mutations = {
    setPolls(state, data) {
        state.Polls = [...data.data]
    },
    setQrTitle(state, title) {
        state.qrTitle = title
    },
    setQrLink(state, link) {
        state.qrLink = link
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
    setPollIndex(state, id) {
        for (let i = 0; i < state.Polls.length; i++) {
            if (state.Polls[i].pollId === id) {
                state.index = i
                return
            }
        }
        state.index = -1
    },
    splicePolls(state, index) {
        state.Polls.splice(index, 1)
    },
}
export const actions = {
    async initialize({ commit, state }) {
        // console.log('reload')
        let error = ''
        const data = await this.$axios.get('/poll').catch((reason) => {
            // console.log(reason)
            error = reason
        })
        if (error.length === 0) {
            // console.log(data)
            commit('setPolls', data)
        } else {
            commit('saveError', error)
        }
    },
    async updatePollStatus({ commit }, pollId) {
        let error = ''
        const data = await this.$axios.post('/addPollStatus/' + pollId).catch((reason) => {
            // console.log(reason)
            error = reason
        })
        if (error.length === 0) {
            commit('setPollStatus', { pollId, data })
        } else {
            commit('saveError', error)
        }
    },
    async reEditPoll({ commit }, pollId) {
        let error = ''
        const data = await this.$axios.post('/removePollStatus/' + pollId).catch((reason) => {
            // console.log(reason)
            error = reason
        })
        if (error.length === 0) {
            commit('setPollStatus', { pollId, data })
        } else {
            commit('saveError', error)
        }
    },
}
