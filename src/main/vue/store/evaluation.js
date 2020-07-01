export const state = () => ({
    DiagramData: {},
    Polls: [],
    FilterList: [],
    pollId: -1,
    DiagramFormat: [],
    defaultDiagramFormat: {
        backgroundColors: ['#aaaaaa'],
        backgroundColor: '#aaaaaa',
        multipleColors: false,
        diagramType: 'bar',
        showDiagram: true,
        showTable: true,
    },
    Sessions: [],
})

function formatToString(format) {
    const short = {
        q: format.questionId,
        d: format.showDiagram,
        t: format.diagramType,
        b: format.backgroundColors,
        c: format.backgroundColor,
        m: format.multipleColors,
        r: format.showTable,
    }
    return JSON.stringify(short)
}

function stringToFormat(string) {
    const short = JSON.parse(string)
    return {
        questionId: short.q,
        showDiagram: short.d,
        diagramType: short.t,
        backgroundColors: short.b,
        backgroundColor: short.c,
        multipleColors: short.m,
        showTable: short.r,
    }
}

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
    getDiagramFormat(state) {
        return (id) => {
            for (let i = 0; i < state.DiagramFormat.length; i++) {
                if (state.DiagramFormat[i].questionId === id) {
                    return state.DiagramFormat[i]
                }
            }
            for (let i = 0; i < state.DiagramData.questionList.length; i++) {
                if (state.DiagramData.questionList[i].questionId === id) {
                    const defaultFormat = JSON.parse(JSON.stringify(state.defaultDiagramFormat))
                    const colors = state.defaultDiagramFormat.backgroundColors
                    for (let q = colors.length; q < state.DiagramData.questionList[i].answerPossibilities.length; q++) {
                        defaultFormat.backgroundColors.push(String(colors[q % colors.length]))
                    }
                    return defaultFormat
                }
            }
            return state.defaultDiagramFormat
        }
    },
}
export const mutations = {
    setDiagramData(state, data) {
        state.DiagramData = data.data
    },
    setPollData(state, pollData) {
        state.Polls = pollData.data
    },
    saveFilter(state, filterList) {
        state.FilterList = filterList
    },
    setSessions(state, data) {
        state.Sessions = data.data
    },
    setDiagramFormatsFromServer(state, formatList) {
        state.defaultDiagramFormat = stringToFormat(formatList[0])
        if (formatList.length > 1) {
            state.DiagramFormat = stringToFormat(formatList.slice(1))
        }
    },
    setDiagramFormats(state, { useOnAll, format }) {
        if (useOnAll) {
            state.DiagramFormat = []
        }
        state.defaultDiagramFormat = format
    },
    setDiagramFormat(state, format) {
        for (let i = 0; i < state.DiagramFormat.length; i++) {
            if (state.DiagramFormat[i].questionId === format.questionId) {
                state.DiagramFormat[i] = format
                return
            }
        }
        state.DiagramFormat.push(format)
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
        commit('setDiagramData', data)
    },
    async updateData({ state, commit }) {
        await this.$axios
            .post('/evaluation/generateDiagram', state.FilterList)
            .catch((reason) => {
                console.log(reason)
            })
            .then((response) => {
                commit('setDiagramData', response)
            })
    },
    async sendFilter({ state, commit }, filterList) {
        commit('saveFilter', filterList)
        await this.$axios
            .post('/evaluation/generateDiagram', filterList)
            .catch((reason) => {
                console.log(reason)
            })
            .then((response) => {
                commit('setDiagramData', response)
            })
    },
    async saveSession({ state, commit }, sessionInfo) {
        const formats = []
        formats.push(formatToString(state.defaultDiagramFormat))
        for (let i = 0; i < state.DiagramFormat.length; i++) {
            formats.push(formatToString(state.DiagramFormat[i]))
        }
        const payload = {
            pollId: state.pollId,
            sessionTitle: sessionInfo.sessionTitle,
            lastUsername: sessionInfo.lastUsername,
            diagramFormat: formats,
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
                commit('setDiagramFormatsFromServer', response.data.diagramFormat)
            })
            .catch((reason) => {
                console.log(reason)
            })
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
    async awaitPollResultText({ state, commit }, fileName) {
        console.log('trying to find file ')
        console.log('api/export/filename:{' + fileName + '}')
        const FileDownload = require('js-file-download')
        const response = await this.$axios.get('/export/getResult/' + fileName).then((response) => {
            FileDownload(response.data, 'Results' + fileName + '.txt')
        })
        console.log('response: ', response)
        return response
    },
}
