import api from '../api/eval'
import api2 from '../api/polls'
export const state = () => ({
    DiagramData: {},
    Polls: {},
    FilterList: {},
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
    initializePolls: (state, pollData) => {
        state.Polls = pollData.data
    },
    saveFilter: (state, filterList) => {
        state.FilterList = filterList
        api.sendFilter(state.FilterList)
    },
}
export const actions = {
    async initialize({ commit }) {
        const data = await api.getInitialDiagrams()
        commit('initializeData', data)
        const pollData = await api2.getPolls()
        commit('initializePolls', pollData)
    },
}
