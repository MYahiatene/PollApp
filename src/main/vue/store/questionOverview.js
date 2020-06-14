export const state = () => ({
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
        textMultiline: true,
        textMinimum: 0,
        textMaximum: 1000,
        choiceType: '',
    },
})
export const mutations = {
    setQuestionMes(state, questionMessage) {
        state.question.questionMessage = questionMessage
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
        state.question.answerPossibilities.push(' ')
    },
    setAnswerP(state, obj) {
        state.question.answerPossibilities[obj.index] = obj.answer
    },
    setNumberOfPossibleAnswers(state, NumberOfPossibleAnswers) {
        state.question.numberOfPossibleAnswers = NumberOfPossibleAnswers
    },
}
export const getters = {
    getQuestionMessage(state) {
        return state.question.questionMessage
    },
    getQuestion(state) {
        return state.question
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
