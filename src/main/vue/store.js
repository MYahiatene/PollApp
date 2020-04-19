import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        authenticated: null,
        articles: [
            {
                id: 1,
                title: "Artikel 1",
                text: "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam"
            },
            {
                id: 2,
                title: "Artikel 2",
                text: "Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr"
            }
        ],
    },
    mutations: {
        saveArticle(state, article) {
            let id = this.state.articles.length
            article.id = id + 1
            this.state.articles.push(article)
        },
        updateArticle(state, article) {
            let index = this.state.articles.findIndex(a => a.id == article.id)
            this.state.articles[index] = article
        },
        authenticate(state, auth) {
            if (auth.username === 'admin' && auth.password === 'password') {
                this.state.authenticated = true
            } else {
                this.state.authenticated = false
            }
        }
    },
    getters: {
        getArticle: (state) => {
            return (id) => {
                return state.articles.find(article => article.id == id)
            }
        },
        isAuthenticated: (state) => {
            return state.authenticated
        }
    }
})
