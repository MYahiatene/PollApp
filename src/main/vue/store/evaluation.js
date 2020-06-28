export const state = () => ({
    DiagramData: {},
    Polls: [],
    FilterList: [],
    pollId: -1,
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
    setDiagramData(state, data) {
        state.DiagramData = data.data
        console.log('DD')
        console.log(state.DiagramData)
    },
    setPollData(state, pollData) {
        state.Polls = pollData.data
        console.log('PD')
        console.log(state.Polls)
    },
    saveFilter(state, filterList) {
        state.FilterList = filterList
    },
    setPollID(state, id) {
        state.pollId = id
        state.FilterList = [
            {
                filterType: 'dataFilter',
                basePollId: id,
                baseQuestionIds: [],
            },
        ]
    },
}

export const actions = {
    async initialize({ commit }, pollId) {
        console.log('INIT')
        console.log(pollId)
        console.log(this.$axios.defaults.headers)
        console.log('getPolls from Server')
        commit('setPollID', pollId)
        const pollData = await this.$axios.get('/poll')
        commit('setPollData', pollData)
        const data = await this.$axios.post('/evaluation/generateDiagram', [
            {
                filterType: 'DataFilter',
                basePollId: pollId,
                baseQuestionIds: [],
            },
        ])
        console.log(data)
        commit('setDiagramData', data)
    },
    updateData({ state, commit }) {
        this.$axios
            .post('/evaluation/generateDiagram', state.FilterList)
            .catch((reason) => {
                console.log(reason)
            })
            .then((response) => {
                console.log(response)
                commit('setDiagramData', response)
            })
    },
    async sendFilter({ state, commit }, filterList) {
        console.log('in store!')
        commit('saveFilter', filterList)
        console.log(filterList)
        await this.$axios
            .post('/evaluation/generateDiagram', filterList)
            .catch((reason) => {
                console.log(reason)
            })
            .then((response) => {
                console.log(response)
                commit('setDiagramData', response)
            })
        console.log('store durch')
    },
}
