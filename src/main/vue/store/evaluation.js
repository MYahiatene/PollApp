export const state = () => ({
    DiagramData: {},
    Polls: {},
    FilterList: {},
})
export const getters = {
    getDiagramData(state) {
        return state.DiagramData.questionList
    },
    getPollName(state) {
        return state.DiagramData.name
    },
    getPolls(state) {
        return state.Polls
    },
}
export const mutations = {
    initializeData(state, data) {
        state.DiagramData = data.data
        console.log('DD')
        console.log(state.DiagramData)
    },
    initializePolls(state, pollData) {
        state.Polls = pollData.data
    },
    saveFilter(state, filterList) {
        state.FilterList = filterList
    },
}

export const actions = {
    async initialize({ commit }) {
        console.log(this.$axios.defaults.headers)
        const data = await this.$axios.post('/evaluation/generateDiagram', [
            {
                filterType: 'DataFilter',
                basePollId: 1,
                baseQuestionIds: [3, 4, 5, 6, 7, 8],
            },
        ])
        commit('initializeData', data)
        const pollData = await this.$axios.get('/poll')
        commit('initializePolls', pollData)
    },
    async sendFilter({ state, commit }, filterList) {
        console.log('in store!')
        commit('saveFilter', filterList)
        const response = await this.$axios.post('/evaluation/generateDiagram', filterList)
        console.log(response)
        commit('initializeData', response)
    },
}
