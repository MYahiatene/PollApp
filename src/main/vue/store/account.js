export const state = () => ({
    failStatus: 0,
})

export const getters = {
    getFailStatus: (state) => {
        return state.failStatus
    },
}

export const mutations = {
    setFailStatus: (state, failStatus) => {
        state.failStatus = failStatus
    },
}

export const actions = {
    async changePassword({ commit }, userObj) {
        this.$axios.defaults.baseURL = 'http://localhost:8088/api/'
        this.$axios.defaults.headers.common.Authorization = 'Bearer ' + localStorage.getItem('user-token')
        console.log('userObj', userObj)
        const status = await this.$axios.put('/changePassword', userObj)
        commit('/setFailStatus', status)
    },
}
