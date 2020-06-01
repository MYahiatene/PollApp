import api from '../api/participant'

/**
 * Defines current state of "things" to be called by Getters.
 * @returns {{visibility: boolean,
 *            numberOfQuestions: number,
 *            poll: [string]}}
 */
export const state = () => ({
    visibility: false,
    numberOfQuestions: 0,
    poll: ['Object'],
    // questions: [],
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
}
/**
 * Sets the poll gotten from showPoll as the current state.
 * @type {{setPoll: mutations.setPoll}}
 */
export const mutations = {
    setPoll: (state, poll) => {
        state.poll.push(poll)
        state.visibility = poll.data.visibility
        state.numberOfQuestions = poll.data.categoryList[0].questionList.length // number of questions in this category
    },
}
/**
 * Defines mapAction showPoll and calls getPoll in api/participant. After it got the poll, it comits the poll,
 * to save it in this store (setPoll), so the mapGetters can acces the data and give it back to the Participant.vue page.
 * @type {{showPoll({commit: *}): Promise<void>}}
 */
export const actions = {
    async showPoll({ commit }) {
        const poll = await api.getPoll() // calls api/participant, which calls PollController, which calls PollService and gives back a poll
        commit('setPoll', poll) // calls the setter int the store to save the poll in the store
    },
}
