/**
 * Defines current state of "things" to be called by Getters.
 * @returns {{visibility: boolean,
 *            changeOfCategories: boolean,
 *            numberOfQuestions: number,
 *            categoryIndex: number
 *            poll: [string]
 *            answer: [string]
 *            category: [string]}}
 *            username: string
 */
export const state = () => ({
    visibility: false,
    changeOfCategories: false,
    numberOfQuestions: 0,
    categoryIndex: 1,
    poll: ['Object'],
    answer: ['Object'],
    category: ['Object'],
    username: '',
});
/**
 * Defines mapGetters for Usage in _id.vue.
 * @type {{getPoll: (function(*): [string]|null),
 *         getNumberOfQuestions: (function(*): number),
 *         getVisibility: (function(*): boolean),
 *         getCategory: (function(*): [string]|null),
 *         getCategoryIndex: (function(*): number),
 *         getChangeOfCategory: (function(*): boolean),
 *         getUsername: (function(*): string),
 *         getAnswer:(function(*): [string]|null)}}
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
        return state.username
    },
    getAnswer: (state) => {
        return state.answer
    },
    getQuestionId: (state) => {
        return state.questionId
    },
};

export const mutations = {
    /**
     * Sets the poll gotten from showPoll as the current state and the features visibility, changeOfCategories,
     * numberOfQuestions and the category too, because they are called from the page directly.
     * @type {{setPoll: mutations.setPoll}}
     */
    setPoll: (state, poll) => {
        state.poll.push(poll);
        state.visibility = poll.data.visibility;
        state.changeOfCategories = poll.data.categoryChange;
        state.numberOfQuestions = poll.data.categoryList[0].questionList.length; // number of questions in this category
        state.category = poll.data.categoryList[0]
    },
    /**
     * Set's the username gotten from showPoll as the current state.
     * @param state
     * @param username
     */
    setUsername: (state, username) => {
        state.username = username
    },
    /**
     * Set's the answer object gotten from showAnswer as the current state.
     * @param state
     * @param answer
     */
    setAnswer: (state, answer) => {
        // console.log('Hi from store setter!')
        state.answer = answer
        // console.log('Hi from store setter after being set')
    },
    /**
     * Sets the next or previous category of the poll as the current one, if there is one, depending on the argument
     * -1 or 1, meaning previous or next category wanted
     * @type {{setCategory: mutations.setCategory}}
     */
    setCategory: (state, args) => {
        const index = state.categoryIndex;
        const categoryLength = state.poll[1].data.categoryList.length;
        if (args === 1) {
            if (index !== categoryLength) {
                state.category = state.poll[1].data.categoryList[index];
                state.categoryIndex = index + 1
            }
        } else if (args === -1) {
            if (index !== 1) {
                state.category = state.poll[1].data.categoryList[index - 2];
                state.categoryIndex = index - 1
            }
        }
    },
    setQuestionId: (state, id) => {
        state.questionId = id
    },
};
export const actions = {
    /**
     * Defines mapAction showPoll and sets the global axios with the token saved in localstorage and the baseURL to get
     * the poll from the PollController in the backend. After it got the poll,it commits the poll,to save it in this
     * store (setPoll), so the mapGetters can access the data and give it back to the _id.vue page. It also
     * get's the username and updates it the same way.
     * @param commit
     * @returns {Promise<void>}
     */
    async showPoll({ commit }, id) {
        // console.log('showPoll id: ', id)
        this.$axios.defaults.baseURL = 'http://localhost:8088/api/';
        this.$axios.defaults.headers.common.Authorization = 'Bearer ' + localStorage.getItem('user-token');
        const poll = await this.$axios.get('/participant/' + id);
        const username = await this.$axios.post('/getUsername', { anonymityStatus: poll.data.anonymityStatus });
        commit('setUsername', username.data);
        commit('setPoll', poll)
    },
    /**
     * Defines mapAction showAnswer and sets the global axios with the token saved in localstorage and the baseURL to get
     * all the answers from one user from the AnswerController in the backend. After it got the answers,it commits
     * the answers,to save it in this store (setAnswer), so the mapGetters can access the data and give it back
     * to the _id.vue page.
     * @param state
     * @param answerObj
     * @returns {Promise<void>}
     */
    async showAnswer(state, answerObj) {
        // console.log('Hi, from participant store pre axios.post')
        this.$axios.defaults.baseURL = 'http://localhost:8088/api/';
        this.$axios.defaults.headers.common.Authorization = 'Bearer ' + localStorage.getItem('user-token');
        console.log('Hi, from participant store pre axios.post');
        const answer = await this.$axios.post('/getPollResult', {
            pollId: answerObj.pollId,
            username: answerObj.username,
        });
        console.log('Hi, from participant store post axios.post');
        state.answer = answer;
        console.log('Hi, from participant store post store.commit');
        console.log(answer)
    },
    /* Eventuell einen Header mitsenden, statt data?, aber eigentlich ja das gleiche...
     *  const options = {
     *  headers: {'X-Custom-Header': 'value'}
     *  };
     *  axios.post('/save', { a: 10 }, options);
     * */
    /**
     * Defines mapAction saveAnswer which saves  an answerObj (AnswerCmd) for a specific question from a specific poll
     * from a specific user. It sets the global axios baseURL and the header with the token from the localstorage and
     * calls the PollController to add an answer.
     * @param state
     * @param answerObj object with (username, questionId, answerList, pollId)
     */
    saveAnswer(state, answerObj) {
        console.log('answer in store');
        console.log(answerObj);
        this.$axios.defaults.baseURL = 'http://localhost:8088/api/';
        this.$axios.defaults.headers.common.Authorization = 'Bearer ' + localStorage.getItem('user-token');
        return this.$axios.post('/poll/' + answerObj.pollId + '/addanswer', answerObj)
        // api.saveAnswers(answerObj)
    },
    async saveAnswerPossibility(state, answer) {
        console.log('pollId: ', answer[2]);
        console.log('questionId: ', answer[1]);
        console.log('newAnswer: ', answer[0]);
        const obj = { value: answer[0] };
        this.$axios.defaults.baseURL = 'http://localhost:8088/api/';
        this.$axios.defaults.headers.common.Authorization = 'Bearer ' + localStorage.getItem('user-token');
        const poll = await this.$axios.post(
            '/poll/' + answer[2] + '/question/' + answer[1] + '/addAnswerPossibility',
            obj
        );
        console.log('after post');
        console.log(poll);
        state.commit('setPoll', poll)
        // state.commit('setCategory', poll.data.cate)
    },
};
