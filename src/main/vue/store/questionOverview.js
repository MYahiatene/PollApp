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
    },
})
export const mutations = {}
export const getters = {}
export const actions = {
    createPoll({ commit }, poll) {},
}
