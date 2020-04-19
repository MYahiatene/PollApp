<template>
    <b-container>
        <b-row class="mt-3">
            <b-col cols="3">
                <b-card header="Seitenleiste">
                    <b-button variant="primary" to="/article/create" v-if="authenticated">Neuen Artikel hinzufügen</b-button>
                    <b-button variant="secondary" to="/login" v-else>Login</b-button>
                </b-card>
            </b-col>
            <b-col>
                <b-card header="Startseite" no-body>
                    <b-list-group flush>
                        <router-link tag="b-list-group-item" :to="'/article/' + article.id" v-for="article in articles" :key="article.id">{{ article.title }}</router-link>
                    </b-list-group>
                </b-card>
            </b-col>
        </b-row>
    </b-container>
</template>

<script>
    import {mapGetters, mapState} from "vuex";

    export default {
        name: "Start",
        computed: {
            ...mapState({
                articles: state => state.articles
            }),
            ...mapGetters({
                authenticated: 'isAuthenticated'
            })
        }
    }
</script>

<style scoped>

</style>