export const state = () => ({
    visibility: true,
})
export const mutations = {
    // gets the attribute visibility from QuestionCreation
    changeVisible(state, value) {
        this.state.visibility = value
    },
}
