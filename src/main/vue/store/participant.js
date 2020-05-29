// import axios from 'axios'
import api from '../api/participant'
// import poll from '../api/poll';
export const state = () => ({
    visibility: false,
    numberOfQuestions: 0,
    poll: ['Object'],
    // questions: [],
})
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
export const mutations = {
    setPoll: (state, poll) => {
        state.poll.push(poll)
        state.visibility = poll.data.visibility
        state.numberOfQuestions = poll.data.categoryList[0].questionList.length // number of questions in this category
    },
}
// What is given back here? Just the questions(over /participant) or the entire poll?
export const actions = {
    async showPoll({ commit }) {
        // const poll = await instance.get('/participant')
        const poll = await api.getPoll() // calls api/participant, which calls PollController, which calls PollService and gives back a poll
        commit('setPoll', poll) // calls the setter int the store to save the poll in the store
    },
}
