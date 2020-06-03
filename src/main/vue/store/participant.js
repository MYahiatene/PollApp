import api from '../api/participant'

/**
 * Defines current state of "things" to be called by Getters.
 * @returns {{visibility: boolean,
 *            numberOfQuestions: number,
 *            poll: [string]}}
 */
export const state = () => ({
    visibility: false,
    changeOfCategories: false,
    numberOfQuestions: 0,
    categoryIndex: 1,
    poll: ['Object'],
    category: ['Object'],
})
/**
 * Defines mapGetters for Usage in Participant.vue.
 * @type {{getPoll: (function(*): [string]|null),
 *         getNumberOfQuestions: (function(*): number),
 *         getVisibility: (function(*): *)}}
 */
export const getters = {
    getVisibility: (state) => {
        return state.visibility
    },
    getNumberOfQuestions: (state) => {
        return state.numberOfQuestions
    },
    getPoll: (state) => {
        return state.poll
    },
    getCategory: (state) => {
        return state.category
    },
    getCategoryIndex: (state) => {
        return state.categoryIndex
    },
    getChangeOfCategories: (state) => {
        return state.changeOfCategories
    },
}
/**
 * Sets the poll gotten from showPoll as the current state.
 * @type {{setPoll: mutations.setPoll}}
 */
export const mutations = {
    setPoll: (state, poll) => {
        state.poll.push(poll)
        state.visibility = poll.data.visibility
        state.changeOfCategories = poll.data.categoryChange
        state.numberOfQuestions = poll.data.categoryList[0].questionList.length // number of questions in this category
        state.category = poll.data.categoryList[0]
    },
    setCategory: (state, args) => {
        const index = state.categoryIndex
        if (args === 1) {
            if (index !== state.poll[1].data.categoryList.length) {
                state.category = state.poll[1].data.categoryList[index]
                state.categoryIndex = index + 1
            }
        } else if (args === -1) {
            if (index !== 1) {
                state.category = state.poll[1].data.categoryList[index - 2]
                state.categoryIndex = index - 1
            }
        }
    },
}
export const actions = {
    /**
     * Defines mapAction showPoll and calls getPoll in api/participant. After it got the poll, it comits the poll,
     * to save it in this store (setPoll), so the mapGetters can acces the data and give it back to the Participant.vue page.
     * @type {{showPoll({commit: *}): Promise<void>}}
     */
    async showPoll({ commit }) {
        const poll = await api.getPoll() // calls api/participant, which calls PollController, which calls PollService and gives back a poll
        commit('setPoll', poll) // calls the setter int the store to save the poll in the store
    },
    /**
     * Defines mapAction saveAnswer which saves  an answerObj (AnswerCmd) for a specific question from a specific poll
     * from a specific user. It calls saveAnswer from api/participant.
     * @param state
     * @param answerObj object with (username, questionId, answerList, pollId)
     */
    saveAnswer(state, answerObj) {
        api.saveAnswers(answerObj)
    },
}
