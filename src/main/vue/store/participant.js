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
export const actions = {
    async showPoll({ commit }) {
        // const poll = await instance.get('/participant')
        const poll = await api.getPoll()
        commit('setPoll', poll)
    },
}
