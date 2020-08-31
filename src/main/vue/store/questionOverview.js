export const state = () => ({
    buildIndex: 0,
    saveButtonStatus: true,
    counter: 0,
    question: {
        categoryId: null,
        questionMessage: '',
        answerPossibilities: [],
        questionType: '',
        userAnswers: false,
        numberOfPossibleAnswers: 1,
        endValue: 10,
        startValue: 0,
        stepSize: 1,
        belowMessage: '',
        aboveMessage: '',
        hideValues: false,
        textMultiline: false,
        textMinimum: 0,
        textMinBool: false,
        textMaximum: 1000,
        textMaxBool: false,
        dropDown: false,
        choiceType: '',
        questionId: null,
        categoryType: 0,
        fileName: '',
    },
})
export const mutations = {
    increment(state) {
        state.counter++
    },
    setCategoryType(state, value) {
        state.question.categoryType = value
    },
    setSaveButtonStatus(state, value) {
        state.saveButtonStatus = value
    },
    setQuestionMes(state, questionMessage) {
        state.question.questionMessage = questionMessage
    },
    setStartValue(state, value) {
        state.question.startValue = value
    },
    setEndValue(state, value) {
        state.question.endValue = value
    },
    setStepSize(state, value) {
        state.question.stepSize = value
    },
    setBelowMessage(state, value) {
        state.question.belowMessage = value
    },
    setAboveMessage(state, value) {
        state.question.aboveMessage = value
    },
    setHideValues(state, value) {
        state.question.hideValues = value
    },
    setTextMultiline(state, value) {
        state.question.textMultiline = value
    },
    setTextMin(state, value) {
        state.question.textMinimum = value
    },
    setTextMax(state, value) {
        state.question.textMaximum = value
    },
    setTextMinBool(state, value) {
        state.question.textMinBool = value
    },
    setTextMaxBool(state, value) {
        state.question.textMaxBool = value
    },
    setChoiceType(state, choiceType) {
        state.question.choiceType = choiceType
    },
    setQuestionType(state, questionType) {
        state.question.questionType = questionType
    },
    setUserAnswers(state, userAnswers) {
        state.question.userAnswers = userAnswers
    },
    setAnswerP(state, obj) {
        while (obj.index >= state.question.answerPossibilities.length) {
            state.question.answerPossibilities.push('')
        }
        state.question.answerPossibilities[obj.index] = obj.answer
        while (state.question.answerPossibilities[state.question.answerPossibilities.length - 1] === '') {
            state.question.answerPossibilities.pop()
        }
    },
    setNumberOfPossibleAnswers(state, NumberOfPossibleAnswers) {
        state.question.numberOfPossibleAnswers = NumberOfPossibleAnswers
    },
    setBuildIndex(state, buildIndex) {
        state.buildIndex = buildIndex
    },
    setQuestion(state, question) {
        state.question = question
    },
    setDropDown(state, value) {
        state.question.dropDown = value
    },
    setFileName(state, fileName) {
        state.question.fileName = fileName
    },
}
export const getters = {
    getQuestionMessage(state) {
        return state.question.questionMessage
    },
    getQuestion(state) {
        return state.question
    },
    getBuildIndex(state) {
        return state.buildIndex
    },
    getSaveButtonStatus(state) {
        return state.saveButtonStatus
    },
    getCounter(state) {
        return state.counter
    },
    getFileName(state) {
        return state.question.fileName
    },
}
export const actions = {
    setQuestionMessage({ commit }, questionMessage) {
        commit('setQuestionMes', questionMessage)
    },
    setAnswerPossibility({ commit }, obj) {
        commit('setAnswerP', obj)
    },
    setFileName({ commit }, fileName) {
        commit('setFileName', fileName)
    },
}
