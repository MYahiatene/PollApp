// import axios from 'axios';
import api from '../api/poll'
export const state = () => ({
    poll: null,
})
export const getters = {
    getPoll: (state) => {
        return state.poll
    },
}
// not used right now, since createPoll is directly called on page.vue
export const mutations = {
    savePoll(state, poll) {
        state.poll = poll
    },
}
// not used right now, since createPoll is directly called on page.vue
export const actions = {
    createPoll({ commit }, poll) {
        api.save(poll)
            .then((res) => {
                const newPoll = res.data
                commit('savePoll', newPoll)
                console.log('PollID: ', newPoll)
            })
            .catch(() => {
                commit('savePoll', null)
                // reject(new Error('Authentification failed'))
            })
    },
    async getCopyPoll({ commit }, id) {
        await this.$axios
            .get('/getonepoll', {
                params: {
                    pollId: id,
                },
            })
            .then((response) => {
                commit('savePoll', response.data)
                console.log('response data: ', response.data)
            })
            .catch((error) => {
                console.log(error)
            })
    },

    async getPollName({ commit }, id) {
        console.log('Hallo aus der pollName')
        const response = await this.$axios.get('/getPollName', {
            params: {
                pollId: id,
            },
        })
        console.log('responsepollName: ', response.data)
        return response.data
    },

    async getAnonType({ commit }, id) {
        console.log('Hallo aus der pollName')
        const response = await this.$axios.get('/getAnonType', {
            params: {
                pollId: id,
            },
        })
        console.log('responsepollAnonStatus: ', response.data)
        return response.data
    },

    async getActDate({ commit }, id) {
        console.log('Hallo aus der ActDate')
        const response = await this.$axios.get('/getActDate', {
            params: {
                pollId: id,
            },
        })
        console.log('responsepollActDate: ', response.data)
        return response.data
    },

    async getDeactDate({ commit }, id) {
        console.log('Hallo aus der DeactDate')
        const response = await this.$axios.get('/getDeactDate', {
            params: {
                pollId: id,
            },
        })
        console.log('responsepollDeactDate: ', response.data)
        return response.data
    },
}
