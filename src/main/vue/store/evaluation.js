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
    getSessions(state) {
        return state.Sessions
    },
    getDiagramOption(state) {
        console.log('getDiagramOptions()')
        return (id) => {
            console.log('suche diagramoption ' + id)
            for (let i = 0; i < state.DiagramOptions.length; i++) {
                if (state.DiagramOptions[i].questionId === id) {
                    console.log('custom ' + i)
                    console.log(state.DiagramOptions[i])
                    return JSON.parse(JSON.stringify(state.DiagramOptions[i]))
                }
            }
            console.log(state)
            return JSON.parse(JSON.stringify(state.defaultDiagramOptions))
        }
    },
    getDiagramFormat(state) {
        return (id) => {
            // console.log('suche nach ' + id)
            for (let i = 0; i < state.DiagramFormat.length; i++) {
                if (state.DiagramFormat[i].questionId === id) {
                    // console.log('custom ' + i)
                    // console.log(state.DiagramFormat[i])
                    return JSON.parse(JSON.stringify(state.DiagramFormat[i]))
                }
            }
            for (let i = 0; i < state.DiagramData.questionList.length; i++) {
                if (state.DiagramData.questionList[i].id === id) {
                    const defaultFormat = JSON.parse(JSON.stringify(state.defaultDiagramFormat))
                    const colors = state.defaultDiagramFormat.backgroundColors
                    for (let q = colors.length; q < state.DiagramData.questionList[i].answerPossibilities.length; q++) {
                        defaultFormat.backgroundColors.push(String(colors[q % colors.length]))
                    }
                    // console.log('default')
                    // console.log(defaultFormat)
                    return defaultFormat
                }
            }
            // console.log('super-gau')
            return JSON.parse(JSON.stringify(state.defaultDiagramFormat))
        }
    },
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
        // console.log('reset')
        // console.log(formatList)
        state.DiagramFormat = []
        state.defaultDiagramFormat = stringToFormat(formatList[0])
        if (formatList.length > 1) {
            state.DiagramFormat.push(stringToFormat(formatList.slice(1)))
        }
    },
    setDiagramFormats(state, { useOnAll, format }) {
        // console.log('setDiagramFormats()')
        // console.log(format)
        if (useOnAll) {
            state.DiagramFormat = []
        }
        state.defaultDiagramFormat = format
    },
    setDiagramFormat(state, format) {
        // console.log('setDiagramFormat()')
        // console.log(format)
        for (let i = 0; i < state.DiagramFormat.length; i++) {
            if (state.DiagramFormat[i].questionId === format.questionId) {
                Vue.set(state.DiagramFormat, i, format)
                // console.log('saved custom')
                return
            }
        }
        console.log('saved default')
        state.DiagramFormat.push(format)
    },
    setDiagramOptionsFromServer(state, optionList) {
        console.log('reset')
        console.log(optionList)
        state.DiagramOptions = []
        state.defaultDiagramOptions = JSON.parse(optionList[0])
        if (optionList.length > 1) {
            state.DiagramOptions.push(JSON.parse(optionList.slice(1)))
        }
    },
    setDiagramOptions(state, payload) {
        console.log('setDiagramOptions()')
        console.log(payload)
        if (payload.useOnAll) {
            state.DiagramOptions = []
        }
        state.defaultDiagramOptions = payload.option
    },
    setDiagramOption(state, option) {
        console.log('setDiagramOption()')
        console.log(option)
        for (let i = 0; i < state.DiagramOptions.length; i++) {
            if (state.DiagramOptions[i].questionId === option.questionId) {
                Vue.set(state.DiagramOptions, i, option)
                console.log('saved custom')
                return
            }
        }
        console.log('saved default')
        state.DiagramOptions.push(option)
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
                console.log(response)
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

    async exportCSV({ state, commit }, pollId) {
        console.log('export start!')

        console.log('/api/export/toCSVPoll/{pollId:' + pollId + '}')

        const response = await this.$axios.post('/export/toCSVPoll/' + pollId)

        console.log('response: ', response)
    },

    async awaitPollText({ state, commit }, fileName) {
        console.log('trying to find file ')

        console.log('api/export/filename:{' + fileName + '}')

        const FileDownload = require('js-file-download')

        const response = await this.$axios.get('/export/getFile/' + fileName).then((response) => {
            FileDownload(JSON.stringify(response.data), fileName + '.txt')
        })

        console.log('response: ', response)

        return response
    },

    async awaitPollResultText({ state, commit }, fileName) {
        console.log('trying to find file ')
        console.log('api/export/filename:{' + fileName + '}')
        const FileDownload = require('js-file-download')
        const response = await this.$axios.get('/export/getResult/' + fileName).then((response) => {
            FileDownload(JSON.stringify(response.data), 'Results' + fileName + '.txt')
        })
        console.log('response: ', response)
        return response
    },

    async awaitPollResultCsv({ state, commit }, fileName) {
        console.log('trying to find file ')
        console.log('api/export/filename:{' + fileName + '}')
        const FileDownload = require('js-file-download')
        const response = await this.$axios.get('/export/getCSV/' + fileName).then((response) => {
            FileDownload(
                response.data
                    .replace(/\\n/g, '\n')
                    .replace(/\\'/g, "'")
                    .replace(/\\"/g, '"')
                    .replace(/\\&/g, '&')
                    .replace(/\\r/g, '\r')
                    .replace(/\\t/g, '\t')
                    .replace(/\\b/g, '\b')
                    .replace(/\\f/g, '\f'),
                'CSV' + fileName + '.csv'
            )
        })
        console.log('response: ', response)
        return response
    },

    async importPoll({ state, commit }, file) {
        console.log('Loaded file: ', file)
        const response = await this.$axios.post('/export/importPoll/' + file)
        console.log('response: ', response)
        return response
    },
}
