import axios from 'axios'
export const state = () => ({
    // visibility: false,
    poll: ['test'],
    // questions: [],
})
/* export const getters = {
    isVisible: (state) => {
        return state.visibility
    },
} */
export const mutations = {
    setPoll: (state, poll) => {
        state.poll.push(poll)
    },
}
export const actions = {
    async showPoll({ commit }) {
        // const poll = await this.$axios.$get('api/participant')
        const poll = await axios.get('https://jsonplaceholder.typicode.com/todos/1')
        commit('setPoll', poll)
    },
}
