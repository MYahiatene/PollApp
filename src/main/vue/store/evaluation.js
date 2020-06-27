export const state = () => ({
    DiagramData: {},
    Polls: [],
    FilterList: {},
})
export const getters = {
    getDiagramData(state) {
        console.log(state.DiagramData)
        return state.DiagramData.questionList
    },
    getPollName(state) {
        return state.DiagramData.name
    },
    getParticipants(state) {
        return state.DiagramData.particpantCount
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
        console.log('PD')
        console.log(state.Polls)
    },
    saveFilter(state, filterList) {
        state.FilterList = filterList
    },
}

export const actions = {
    async initialize({ commit }, pollId) {
        console.log('INIT')
        console.log(pollId)
        console.log(this.$axios.defaults.headers)
        console.log('getPolls from Server')
        const pollData = await this.$axios.get('/poll')
        commit('initializePolls', pollData)
        const data = await this.$axios.post('/evaluation/generateDiagram', [
            {
                filterType: 'DataFilter',
                basePollId: pollId,
                baseQuestionIds: [],
            },
        ])
        console.log(data)
        commit('initializeData', data)
    },

    async getCQLength({ commit }, pollId) {
        const pollData = await this.$axios.get('/poll/'+pollId+'/ConsistencyQuestions')
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
