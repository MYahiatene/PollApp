export const state = () => ({
    visibility: false,
})
export const getters = {
    isVisible: (state) => {
        return state.visibility
    },
}
