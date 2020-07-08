export const state = () => ({
    failStatus: 0,
    isAdmin: false,
    isCreator: false,
    isEditor: false,
})

export const getters = {
    getFailStatus: (state) => {
        return state.failStatus
    },
    getIsAdmin: (state) => {
        return state.isAdmin
    },
    getIsCreator: (state) => {
        if ((state.isCreator || state.isAdmin) === true) return true
        else return false
    },
    getIsEditor: (state) => {
        if ((state.isCreator || state.isAdmin || state.isEditor) === true) return true
        else return false
    },
}

export const mutations = {
    setFailStatus: (state, failStatus) => {
        state.failStatus = failStatus
    },
    setRole: (state, role) => {
        if (role === 'Admin') state.isAdmin = true
        else if (role === 'Creator') state.isCreator = true
        else if (role === 'Editor') state.isEditor = true
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
