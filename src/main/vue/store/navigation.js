import api from '../api/polls'
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
        console.log(state.Polls)
    },
}
export const actions = {
    async initialize({ commit }) {
        const data = await api.getPolls()
        commit('setPolls', data)
    },
}
