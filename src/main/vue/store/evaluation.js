export const state = () => ({
    DiagramData: {},
    Polls: {},
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
    getPolls(state) {
        return state.Polls
    },
    getFileName(state) {
        return state.Polls.pollId
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
    async initialize({ commit }, pollId) {
        console.log('INIT')
        console.log(pollId)
        console.log(this.$axios.defaults.headers)
        const data = await this.$axios.post('/evaluation/generateDiagram', [
            {
                filterType: 'DataFilter',
                basePollId: pollId,
                baseQuestionIds: [],
            },
        ])
        console.log(data)
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
    async exportAnswers({ state, commit }, pollId) {
        /** Export von einem Poll */
        console.log('export start!')
        console.log('/api/export/toJSONPoll/{pollId:' + pollId + '}')
        const response = await this.$axios.post('/export/toJSONPoll/' + pollId)
        console.log('response: ', response)
    },
    async exportResults({ state, commit }, pollId) {
        console.log('export start!')
        console.log('/api/export/toJSONPollResult/{pollId:' + pollId + '}')
        const response = await this.$axios.post('/export/toJSONPollResult/' + pollId)
        console.log('response: ', response)
    },
    async awaitPollText({ state, commit }, fileName) {
        console.log('trying to find file ')
        console.log('api/export/filename:{' + fileName + '}')
        const FileDownload = require('js-file-download')
        const response = await this.$axios.get('/export/getFile/' + fileName).then((response) => {
            FileDownload(response.data, fileName + '.txt')
        })
        console.log('response: ', response)
        return response
    },
}
