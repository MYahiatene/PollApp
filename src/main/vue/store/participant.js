// mport axios from 'axios'
export const state = () => ({
    // visibility: false,
    poll: null,
    // questions: [],
})
/* export const getters = {
    isVisible: (state) => {
        return state.visibility
    },
} */
export const mutations = {
    setPoll: (state, poll) => {
        this.state.poll = poll
    },
}
const actions = {
    /* async showPoll (commit){
        return new Promise((resolve, reject)) =>{
            api.participant.getPoll().then(res => {
                commit('setPoll', res.data)
                resolve()
            }).catch(() => {
                commit('setPoll', null)
                reject()
            })
        }
    } */
    async showPoll() {
        const { poll } = await this.$axios.$get('/api/participant')
        this.commit('setPoll', poll)
        return { poll }
    },
}
