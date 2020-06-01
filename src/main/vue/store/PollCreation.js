// import axios from 'axios';
import api from '../api/poll'
export const state = () => ({
    poll: null,
})
// not used right now, since createPoll is directly called on page.vue
export const mutations = {
    savePoll(state, poll) {
        this.poll = poll
    },
}
// not used right now, since createPoll is directly called on page.vue
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
