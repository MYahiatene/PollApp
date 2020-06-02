import api from '../api/polls'
export const state = () => ({
    Polls: {},
    IDsToLoad: {
        pollID: 0,
        categoryID: 0,
        questionID: 0,
    },
})

export const getters = {
    getPolls: (state) => {
        return state.Polls
    },
    questionToLoad: (state) => {
        if (state.IDsToLoad !== undefined && state.IDsToLoad.questionID !== undefined) {
            return state.IDsToLoad.questionID
        }
        return 0
    },
    getQuestion: (state) => {
        if (getters.questionToLoad(state) === 0) {
            return null
        }
        const indices = getters.getIndices(state)
        if (indices !== null) {
            return state.Polls[indices.p].categoryList[indices.c].questionList[indices.q]
        }
        return null
    },
    /* Id's to indices */
    getIndices: (state) => {
        if (state.IDsToLoad === undefined) {
            return null
        }
        const noCategory = -1
        const noQuestion = -1
        for (let p = 0; p < state.Polls.length; p++) {
            if (state.Polls[p].pollId === state.IDsToLoad.pollID) {
                const categories = state.Polls[p].categoryList
                for (let c = 0; c < categories.length; c++) {
                    if (categories[c].categoryId === state.IDsToLoad.categoryID) {
                        const questions = categories[c].questionList
                        for (let q = 0; q < questions.length; q++) {
                            if (questions[q].questionId === state.IDsToLoad.questionID) {
                                return { p, c, q }
                            }
                        }
                        return { p, c, noQuestion }
                    }
                }
                return { p, noCategory, noQuestion }
            }
        }
        return null
    },
}

export const mutations = {
    initializeData: (state, data) => {
        state.Polls = data.data
    },
    saveData: (state, data) => {
        state.Polls = data
    },
    setToLoad: (state, ids) => {
        state.IDsToLoad = ids
        const indices = getters.getIndices(state)
        if (indices === null) {
            state.IDsToLoad.pollID = 0
            state.IDsToLoad.categoryID = 0
            state.IDsToLoad.questionID = 0
        }
        console.log(state.IDsToLoad)
    },
    resetToLoad: (state) => {
        state.IDsToLoad.pollID = 0
        state.IDsToLoad.categoryID = 0
        state.IDsToLoad.questionID = 0
        console.log('ids resetted')
    },
    setPollName: (state, name) => {
        state.Polls.pollName = name
    },
    addQuestion: (state, newId) => {
        console.log('add' + newId)
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            const newQuestion = { questionId: newId }
            console.log(newQuestion)
            state.Polls[indices.p].categoryList[indices.c].questionList.push(newQuestion)
        }
    },
    removeQuestion: (state) => {
        console.log('removeQuestion')
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            delete state.Polls[indices.p].categoryList[indices.c].questionList[indices.q]
        }
    },
    setQuestionMessage: (state, message) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].questionMessage = message
        }
    },
    setQuestionType: (state, type) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].questionType = type
        }
    },
    setNrOfPossibleAnswers: (state, number) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].numberOfPossibleAnswers = number
        }
    },
    setUserAnswers: (state, active) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].userAnswers = active
        }
    },
    updateAnswer: (state, payload) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            const answers = state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].answerPossibilities
            answers[payload.index] = payload.text
            let endIndex = answers.length - 1
            if (answers[endIndex].length > 0) {
                answers.push('')
            } else {
                endIndex--
                while (endIndex > 0 && answers[endIndex].length === 0) {
                    console.log(endIndex)
                    answers.pop()
                    endIndex--
                }
            }
        }
    },
    sendQuestion: (state) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            const payload = {
                pollId: state.IDsToLoad.pollID,
                question: state.Polls[indices.p].categoryList[indices.c].questionList[indices.q],
            }
            api.addQuestion(payload)
        }
    },
    setIntervalStart: (state, value) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].intervalStart = value
        }
    },
    setIntervalFinish: (state, value) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].intervalFinish = value
        }
    },
    setIntervalStep: (state, value) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].intervalStep = value
        }
    },
    setIntervalLowerText: (state, value) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].intervalLowerText = value
        }
    },
    setIntervalUpperText: (state, value) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].intervalUpperText = value
        }
    },
    setIntervalNoAnswer: (state, value) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].intervalNoAnswer = value
        }
    },
    setTextMultiline: (state, value) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].textMultiline = value
        }
    },
    setTextMin: (state, value) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].textMinimum = value
        }
    },
    setTextMax: (state, value) => {
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].textMaximum = value
        }
    },
}
export const actions = {
    async initialize({ commit }) {
        const data = await api.getPolls()
        commit('resetToLoad')
        commit('initializeData', data)
    },
    async createCategory({ state }) {
        console.log('createCategory')
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            console.log('requesting Category')
            const newId = await api.addCategory({ pollId: state.IDsToLoad.pollID, name: 'Neue Kategorie' })
            const newCategory = {
                categoryId: newId,
                categoryName: 'Neue Kategorie',
                pollId: state.IDsToLoad.pollID,
                questionList: [],
            }
            console.log('Create Category')
            state.Polls[indices.p].categoryList.push(newCategory)
        }
    },
    async createQuestion({ commit, state }) {
        console.log('requesting Question')
        const response = await api.addQuestion({
            pollId: 1,
            questionMessage: '',
            answerPossibilities: [''],
            questionType: '',
        })
        console.log(response)
        const newId = response.data
        console.log('Create Question')
        console.log(newId)
        const newQuestion = {}
        newQuestion.questionId = newId
        console.log(newQuestion)
        commit('addQuestion', newId)
    },
    async deleteQuestion({ commit, state }) {
        if (confirm('Soll diese Frage wirklich gelöscht werden?')) {
            commit('removeQuestion')
            const payload = {
                pollId: state.IDsToLoad.pollID,
                questionId: state.IDsToLoad.questionID,
            }
            console.log(payload)
            const result = await api.deleteQuestion(payload)
            commit('resetToLoad')
            console.log(result)
        }
    },
}
