import api from '../api/eval'
export const state = () => ({
    DiagramData: {},
})
export const getters = {
    getDiagramData: (state) => {
        return state.DiagramData.questionList
    },
    getPollName: (state) => {
        return state.DiagramData.name
    },
}
export const mutations = {
    initializeData: (state, data) => {
        console.log(data)
        state.DiagramData = data.data
    },
}
export const actions = {
    async initialize({ commit }) {
        const data = await api.getInitialDiagrams()
        commit('initializeData', data)
    },
}
