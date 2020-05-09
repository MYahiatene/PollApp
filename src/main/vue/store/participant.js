// import axios from 'axios'
export const state = () => ({
    // visibility: false,
    // poll: null,
    questions: [],
})
/* export const getters = {
    isVisible: (state) => {
        return state.visibility
    },
} */
export const mutations = {
    setQuestions: (state, questions) => {
        state.questions = questions
    },
}
/* export default {
    async asyncData() {
        // const poll = axios.get('/api/poll')
        const { questions } = await axios.get('/api/questions')
        this.commit('setQuestions', questions)

        return { questions }
    },
} */
