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
        if (state.IDsToLoad !== undefined) {
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
                    }
                }
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
    setQuestionMessage: (state, message) => {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].questionMessage = message
        }
    },
    setQuestionType: (state, type) => {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].questionType = type
        }
    },
    setNrOfPossibleAnswers: (state, number) => {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].numberOfPossibleAnswers = number
        }
    },
    setUserAnswers: (state, active) => {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].userAnswers = active
        }
    },
    addAnswer: (state, answer) => {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].answerPossibilities.push(answer)
        }
    },
    updateAnswer: (state, payload) => {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].answerPossibilities[payload.index] =
                payload.text
        }
    },
    removeAnswer: (state) => {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Polls[indices.p].categoryList[indices.c].questionList[indices.q].answerPossibilities.pop()
        }
    },
    sendQuestion(state) {
        console.log('save')
        const indices = getters.getIndices(state)
        console.log(indices)
        if (indices !== null) {
            const payload = {
                pollId: state.IDsToLoad.pollID,
                question: state.Polls[indices.p].categoryList[indices.c].questionList[indices.q],
            }
            api.saveQuestion(payload)
        }
    },
}
export const actions = {
    async initialize({ commit }) {
        const data = await api.getPolls()
        commit('initializeData', data)
    },
}
