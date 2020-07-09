import Vue from 'vue'

const generateDefaultState = () => {
    return {
        DiagramData: {},
        Polls: [],
        FilterList: [],
        pollId: -1,
        currentSession: -1,
        DiagramFormat: [],
        defaultDiagramFormat: {
            questionId: -1,
            backgroundColors: ['#aaaaaa'],
            backgroundColor: '#aaaaaa',
            multipleColors: false,
            diagramType: 'bar',
            showDiagram: true,
            showTable: true,
        },
        DiagramOptions: [],
        defaultDiagramOptions: {
            questionId: -1,
            cumulated: false,
            relative: false,
        },
        Sessions: [],
    }
}

export const state = generateDefaultState()

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
    console.log('hi')
    console.log(string)
    const short = JSON.parse(string)
    console.log(short)
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
    getPollId(state) {
        return state.pollId
    },
    getFilterList(state) {
        return state.FilterList
    },
    getParticipants(state) {
        return state.DiagramData.particpantCount
    },
    getCurrentSession(state) {
        return state.currentSession
    },
    getPolls(state) {
        return state.Polls
    },
    getCurrentPoll(state) {
        for (let i = 0; i < state.Polls.length; i++) {
            if (state.Polls[i].pollId === state.pollId) {
                return state.Polls[i]
            }
        }
        return {}
    },
    getSessions(state) {
        return state.Sessions
    },
    getDiagramOption(state) {
        console.log('getDiagramOptions()')
        return (id) => {
            for (let i = 0; i < state.DiagramOptions.length; i++) {
                if (state.DiagramOptions[i].questionId === id) {
                    return JSON.parse(JSON.stringify(state.DiagramOptions[i]))
                }
            }
            return JSON.parse(JSON.stringify(state.defaultDiagramOptions))
        }
    },
    getDiagramFormat(state) {
        return (id) => {
            for (let i = 0; i < state.DiagramFormat.length; i++) {
                if (state.DiagramFormat[i].questionId === id) {
                    return JSON.parse(JSON.stringify(state.DiagramFormat[i]))
                }
            }
            for (let i = 0; i < state.DiagramData.questionList.length; i++) {
                if (state.DiagramData.questionList[i].id === id) {
                    const defaultFormat = JSON.parse(JSON.stringify(state.defaultDiagramFormat))
                    while (
                        defaultFormat.backgroundColors.length >
                        state.DiagramData.questionList[i].answerPossibilities.length
                    ) {
                        defaultFormat.backgroundColors.pop()
                    }
                    const colors = state.defaultDiagramFormat.backgroundColors
                    for (let q = colors.length; q < state.DiagramData.questionList[i].answerPossibilities.length; q++) {
                        defaultFormat.backgroundColors.push(String(colors[q % colors.length]))
                    }
                    return defaultFormat
                }
            }
            return JSON.parse(JSON.stringify(state.defaultDiagramFormat))
        }
    },

    // getUserList(state) {
    //     return (id) => {}
    // },
}
export const mutations = {
    resetState(state) {
        Object.assign(state, generateDefaultState())
    },
    setDiagramData(state, data) {
        console.log(data)
        state.DiagramData = data.data
    },
    setPollData(state, pollData) {
        state.Polls = [pollData.data]
    },
    saveFilter(state, filterList) {
        state.FilterList = filterList
    },
    setSessions(state, data) {
        state.Sessions = data.data
    },
    setCurrentSession(state, sessionId) {
        state.currentSession = sessionId
    },
    setDiagramFormatsFromServer(state, formatList) {
        state.DiagramFormat = []
        state.defaultDiagramFormat = stringToFormat(formatList[0])
        formatList = formatList.slice(1)
        while (formatList.length > 0) {
            state.DiagramFormat.push(stringToFormat(formatList[0]))
            formatList = formatList.slice(1)
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
                Vue.set(state.DiagramFormat, i, format)
                return
            }
        }
        state.DiagramFormat.push(format)
    },
    setDiagramOptionsFromServer(state, optionList) {
        state.DiagramOptions = []
        state.defaultDiagramOptions = JSON.parse(optionList[0])
        if (optionList.length > 1) {
            state.DiagramOptions.push(JSON.parse(optionList.slice(1)))
        }
    },
    setDiagramOptions(state, payload) {
        if (payload.useOnAll) {
            state.DiagramOptions = []
        }
        state.defaultDiagramOptions = payload.option
    },
    setDiagramOption(state, option) {
        for (let i = 0; i < state.DiagramOptions.length; i++) {
            if (state.DiagramOptions[i].questionId === option.questionId) {
                Vue.set(state.DiagramOptions, i, option)
                return
            }
        }
        state.DiagramOptions.push(option)
    },
    setPollID(state, id) {
        state.pollId = Number(id)
        state.FilterList = [
            {
                filterType: 'dataFilter',
                basePollId: Number(id),
                baseQuestionIds: [],
            },
        ]
    },
}
export const actions = {
    async initialize({ commit }, pollID) {
        commit('setPollID', pollID)
        const pollData = await this.$axios.get('/getonepoll', {
            params: {
                pollId: pollID,
            },
        })
        commit('setPollData', pollData)
        const data = await this.$axios.post('/evaluation/generateDiagram', [
            {
                filterType: 'DataFilter',
                basePollId: pollID,
                baseQuestionIds: [-1],
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
        const options = []
        options.push(JSON.stringify(state.defaultDiagramOptions))
        for (let i = 0; i < state.DiagramOptions.length; i++) {
            options.push(JSON.stringify(state.DiagramOptions[i]))
        }
        const payload = {
            pollId: state.pollId,
            sessionId: sessionInfo.sessionId,
            sessionTitle: sessionInfo.sessionTitle,
            lastUsername: sessionInfo.lastUsername,
            diagramFormat: formats,
            diagramOptions: options,
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
                commit('setCurrentSession', sessionId)
                commit('saveFilter', response.data.filterList)
                commit('setDiagramFormatsFromServer', response.data.diagramFormat)
                commit('setDiagramOptionsFromServer', response.data.diagramOptions)
            })
            .catch((reason) => {
                console.log(reason)
            })
    },
    async deleteSession({ state, commit }, sessionId) {
        console.log('loadSession(' + sessionId + ')')
        await this.$axios
            .get('/evaluation/deleteSession/' + sessionId)
            .then((response) => {
                if (sessionId === state.currentSession) {
                    commit('setCurrentSession', -1)
                    commit('saveFilter', [])
                    commit('setDiagramFormatsFromServer', [
                        {
                            questionId: -1,
                            backgroundColors: ['#aaaaaa'],
                            backgroundColor: '#aaaaaa',
                            multipleColors: false,
                            diagramType: 'bar',
                            showDiagram: true,
                            showTable: true,
                        },
                    ])
                }
            })
            .catch((reason) => {
                console.log(reason)
            })
    },
    async exportPoll({ state, commit }, { pollId, formatString, customSeparator }) {
        /** Export von einem Poll */
        console.log('export start!')
        console.log('/api/export/Poll/{pollId:' + pollId + '}')
        let response
        await this.$axios
            .post('/export/Poll/' + pollId, null, {
                params: {
                    format: formatString,
                    separator: customSeparator,
                },
            })
            .catch((error) => {
                console.log(error)
            })
            .then((r) => {
                response = r
            })
        console.log('response: ', response)
        return response.data
    },

    async exportResults({ state, commit }, { pollId, sessionID, formatString, customSeparator }) {
        console.log('export start!')
        console.log('/api/export/PollResult/{pollId:' + pollId + '}')
        let response
        await this.$axios
            .post('/export/PollResult/' + pollId, state.FilterList, {
                params: {
                    sessionId: sessionID,
                    format: formatString,
                    separator: customSeparator,
                },
            })
            .catch((error) => {
                console.log(error)
            })
            .then((r) => {
                response = r
            })
        console.log('r: ', response)
        return response.data
    },

    async awaitFile({ state, commit }, { fileName, fileNumber, fileFormat }) {
        console.log('trying to find file ')
        console.log('api/export/filename:{' + fileNumber + '}')
        const FileDownload = require('js-file-download')
        const response = await this.$axios
            .get('/export/getFile/' + fileNumber)
            .then((response) => {
                let finalData
                if (fileFormat === 'csv') {
                    finalData = response.data
                        .replace(/\\n/g, '\n')
                        .replace(/\\'/g, "'")
                        .replace(/\\"/g, '"')
                        .replace(/\\&/g, '&')
                        .replace(/\\r/g, '\r')
                        .replace(/\\t/g, '\t')
                        .replace(/\\b/g, '\b')
                        .replace(/\\f/g, '\f')
                } else if (fileFormat === 'json') {
                    finalData = JSON.stringify(response.data)
                }
                FileDownload(finalData, fileName + '.' + fileFormat)
            })
            .catch((error) => {
                console.log(error)
                return false
            })
        console.log('response: ', response)
        return true
    },

    async importPoll({ state, commit }, file) {
        console.log('Loaded file: ', JSON.parse(file)) // .replace(/#/g, encodeURIComponent('#')))
        const data = JSON.parse(file)
        const response = await this.$axios.$post('/export/importPoll/', data).catch((error) => {
            console.log(error)
            return false
        })
        console.log('Response: ', response)
        return true
    },
}
