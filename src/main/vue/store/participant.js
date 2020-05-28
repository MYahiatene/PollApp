// import axios from 'axios'
import api from '../api/participant'
// import poll from '../api/poll';
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
        console.log('Hey', state.poll[1].data.pollId)
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
    async showPoll({ commit }) {
        // const poll = await instance.get('/participant')
        console.log('vor dem Aufruf')
        const poll = await api.getPoll() // calls api/participant, which calls PollController, which calls PollService and gives back a poll
        console.log('nach dem Aufruf')
        console.log(poll)
        commit('setPoll', poll) // calls the setter int the store to save the poll in the store
    },
}
