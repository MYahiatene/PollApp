// import axios from 'axios'
import api from '../api/participant'
export const state = () => ({
    // visibility: false,
    poll: ['Object'],
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
// What is given back here? Just the questions(over /participant) or the entire poll?
export const actions = {
    async getPoll({ commit }) {
        // const poll = await instance.get('/participant')
        const poll = await api.getPoll() // calls api/participant, which calls PollController, which calls PollService and gives back a poll
        commit('setPoll', poll) // what is this?
    },
}
