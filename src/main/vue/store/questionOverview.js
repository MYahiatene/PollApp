export const state = () => ({
    buildIndex: 0,
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
        choiceType: '',
        questionId: null,
    },
})
export const mutations = {
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
    addAnswer(state) {
        state.question.answerPossibilities.push('')
    },
    setAnswerP(state, obj) {
        state.question.answerPossibilities[obj.index] = obj.answer
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
}
export const actions = {
    setQuestionMessage({ commit }, questionMessage) {
        commit('setQuestionMes', questionMessage)
    },
    setAnswerPossibility({ commit }, obj) {
        commit('setAnswerP', obj)
    },
}
