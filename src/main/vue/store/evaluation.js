export const state = () => ({
    DiagramData: {},
    Polls: [],
    FilterList: [],
    pollId: -1,
    Sessions: [],
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
    getSessions(state) {
        return state.Sessions
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
    setSessions(state, data) {
        state.Sessions = data.data
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
    async saveSession({ state, commit }, sessionInfo) {
        console.log('saveSession()')
        console.log(sessionInfo)
        const payload = {
            pollId: state.pollId,
            sessionTitle: sessionInfo.sessionTitle,
            lastUsername: sessionInfo.lastUsername,
            diagramColors: [],
            filterList: state.FilterList,
        }
        await this.$axios
            .post('/evaluation/saveSession', payload)
            .then((response) => {
                console.log(response)
            })
            .catch((reason) => {
                console.log(reason)
            })
    },

    async loadSessions({ state, commit }) {
        console.log('getSessions()')
        await this.$axios
            .get('/evaluation/getSessions/' + state.pollId)
            .then((response) => {
                console.log(response)
                commit('setSessions', response)
            })
            .catch((reason) => {
                console.log(reason)
            })
    },

    async loadSession({ state, commit }, sessionId) {
        console.log('loadSession(' + sessionId + ')')
        await this.$axios
            .get('/evaluation/loadSession/' + sessionId)
            .then((response) => {
                console.log(response)
                commit('saveFilter', response.data.filterList)
            })
            .catch((reason) => {
                console.log(reason)
            })
    },
}
