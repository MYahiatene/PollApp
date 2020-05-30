import api from '../api/eval'
export const state = () => ({
    DiagramData: {},
    Polls: {},
})
export const getters = {
    getDiagramData: (state) => {
        return state.DiagramData.questionList
    },
    getPollName: (state) => {
        return state.DiagramData.name
    },
    getPolls: (state) => {
        return state.Polls
    },
}
export const mutations = {
    initializeData: (state, data) => {
        state.DiagramData = data.data
    },
}
export const actions = {
    async initialize({ commit }) {
        const data = await api.getInitialDiagrams()
        commit('initializeData', data)
        const pollData = await api.getPolls()
        commit('getPolls', pollData)
    },
}
