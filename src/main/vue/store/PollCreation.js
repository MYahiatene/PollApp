export const state = () => ({
    // visibility: false,
})
export const mutations = {
    // sets the attribute visibility
    changeVisible(state) {
        state.visibility = !state.visibility
    },
    savePoll(state, poll) {
        // let id = this.state.articles.length
        // article.id = id + 1
        // this.state.articles.push(article)
        this.poll.push(poll)
    },
} /*
export const actions: {
savePoll(){
    this.commit('savePoll','polltest') etc.
} })
    },
} */
