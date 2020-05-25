// import axios from 'axios';
import api from '../api/poll'
export const state = () => ({
    poll: null,
})
export const mutations = {
    savePoll(state, poll) {
        this.poll = poll
    },
}
export const actions = {
    createPoll({ commit }, poll) {
        api.save(poll)
            .then((res) => {
                const newPoll = res.data
                commit('savePoll', newPoll)
            })
            .catch(() => {
                commit('savePoll', null)
                // reject(new Error('Authentification failed'))
            })
    },
}
