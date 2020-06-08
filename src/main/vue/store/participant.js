/**
 * Defines current state of "things" to be called by Getters.
 * @returns {{visibility: boolean,
 *            changeOfCategories: boolean,
 *            numberOfQuestions: number,
 *            categoryIndex: number
 *            poll: [string]
 *            category: [string]}}
 */
export const state = () => ({
    visibility: false,
    changeOfCategories: false,
    numberOfQuestions: 0,
    categoryIndex: 1,
    poll: ['Object'],
    category: ['Object'],
    username: '',
})
/**
 * Defines mapGetters for Usage in Participant.vue.
 * @type {{getPoll: (function(*): [string]|null),
 *         getNumberOfQuestions: (function(*): number),
 *         getVisibility: (function(*): boolean),
 *         getCategory: (function(*): [string]|null),
 *         getCategoryIndex: (function(*): number),
 *         getChangeOfCategory: (function(*): boolean)}}
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
    getUsername: (state) => {
        // console.log('getUsername: ', state.username)
        return state.username
    },
}

export const mutations = {
    /**
     * Sets the poll gotten from showPoll as the current state.
     * @type {{setPoll: mutations.setPoll}}
     */
    setPoll: (state, poll) => {
        state.poll.push(poll)
        state.visibility = poll.data.visibility
        state.changeOfCategories = poll.data.categoryChange
        state.numberOfQuestions = poll.data.categoryList[0].questionList.length // number of questions in this category
        state.category = poll.data.categoryList[0]
    },
    setUsername: (state, username) => {
        state.username = username
    },
    /**
     * Sets the next or previous category of the poll as the current one, if there is one, depending on the argument
     * -1 or 1, meaning previous or next category wanted
     * @type {{setCategory: mutations.setCategory}}
     */
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
     * Defines mapAction showPoll and sets the global axios with the token saved in localstorage and the baseURL to get
     * the poll from the PollController in the backend. After it got the poll,it commits the poll,to save it in this
     * store (setPoll), so the mapGetters can access the data and give it back to the Participant.vue page.
     * @type {{showPoll({commit: *}): Promise<void>}}
     */
    async showPoll({ commit }) {
        this.$axios.defaults.baseURL = 'http://localhost:8088/api/'
        this.$axios.defaults.headers.common.Authorization = 'Bearer ' + localStorage.getItem('user-token')
        const poll = await this.$axios.get('/participant')
        const username = await this.$axios.post('/getUsername', { anonymityStatus: poll.data.anonymityStatus })
        commit('setUsername', username.data)
        commit('setPoll', poll) // calls the setter int the store to save the poll in the store
    },
    /**
     * Defines mapAction saveAnswer which saves  an answerObj (AnswerCmd) for a specific question from a specific poll
     * from a specific user. It sets the global axios baseURL and the header with the token from the localstorage and
     * calls the PollController to add an answer.
     * @param state
     * @param answerObj object with (username, questionId, answerList, pollId)
     */
    saveAnswer(state, answerObj) {
        this.$axios.defaults.baseURL = 'http://localhost:8088/api/'
        this.$axios.defaults.headers.common.Authorization = 'Bearer ' + localStorage.getItem('user-token')
        return this.$axios.post('/poll/pollId:1/addanswer', answerObj)
        // api.saveAnswers(answerObj)
    },
}
