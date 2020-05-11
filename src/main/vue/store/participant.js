// mport axios from 'axios'
export const state = () => ({
    // visibility: false,
    poll: null,
    // questions: [],
})
export const getters = {
    /* isVisible: (state) => {
        return state.visibility
    }, */
    getPoll: (state) => {
        return state.poll
    },
}
export const mutations = {
    setPoll: (state, poll) => {
        state.poll.push(poll)
    },
}
export const actions = {
    async showPoll({ commit }) {
        const poll = await this.$axios.$get('http://localhost:8088/api/participant')
        // const poll = await axios.get('https://jsonplaceholder.typicode.com/todos/1')
        commit('setPoll', poll)
    },
}
