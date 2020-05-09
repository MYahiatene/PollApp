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

        //no idea what I'm doing here...
    },
}
actions: {
    savePoll({commit}, poll) { // TODO: Find out what's wrong here
        return new Promise((resolve, reject) => {
            api.poll.save(article).then(res => {    // using save() from api.poll
                commit('savePoll', res.data)
                resolve()
            }).catch(() => {
                reject()
            })
        })
    },
}
