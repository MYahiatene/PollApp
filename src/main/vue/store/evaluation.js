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
    },
}

export const actions = {
    async initialize({ commit }) {
        console.log(this.$axios.defaults.headers)
        const data = await this.$axios.get('/evaluation/initialDiagrams')
        commit('initializeData', data)
        const pollData = await this.$axios.get('/poll')
        commit('initializePolls', pollData)
    },
    async sendFilter({ state, commit }, filterList) {
        commit('saveFilter', filterList)
        await this.$axios.post('/evaluation/generateDiagram', filterList)
    },
}
