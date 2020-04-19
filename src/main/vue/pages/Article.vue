<template>
    <b-container class="mt-3">
        <b-row>
            <b-col>
                <router-link to="/">Zurück zur Startseite</router-link>
                <h1>{{ article.title }}</h1>
                <p>{{ article.text }}</p>
                <router-link tag="b-button" variant="secondary" :to="'/article/' + id + '/edit'" v-if="isAuthenticated">Artikel bearbeiten</router-link>
            </b-col>
        </b-row>
    </b-container>
</template>

<script>
    import {mapGetters} from "vuex";

    export default {
        name: "Article",
        data() {
            return {
                id: 0
            }
        },
        created() {
            this.id = this.$route.params.id
        },
        computed: {
            ...mapGetters(['getArticle', 'isAuthenticated']),
            article() {
                return this.getArticle(this.id)
            }
        }
    }
</script>

<style scoped>

</style>