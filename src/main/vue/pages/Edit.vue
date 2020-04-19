<template>
    <b-container class="mt-3">
        <b-row>
            <b-col>
                <b-card header="Artikel bearbeiten">
                    <b-form @submit.prevent="save">
                        <b-form-group label="Titel:" label-cols="3">
                            <b-form-input type="text" v-model="article.title"></b-form-input>
                        </b-form-group>
                        <b-form-group label="Text:" label-cols="3">
                            <b-form-textarea type="textarea" rows="10" v-model="article.text"></b-form-textarea>
                        </b-form-group>
                        <b-button variant="primary" type="submit">Speichern</b-button>
                    </b-form>
                </b-card>
            </b-col>
        </b-row>
    </b-container>
</template>

<script>
    import {mapGetters, mapMutations} from "vuex";

    export default {
        name: "Edit",
        data() {
            return {
                id: 0,
                article: {}
            }
        },
        mounted() {
            this.id = this.$route.params.id
            this.article = this.getArticle(this.id)
        },
        computed: {
            ...mapGetters(['getArticle']),
        },
        methods: {
            ...mapMutations([
                'updateArticle'
            ]),
            save() {
                this.updateArticle(this.article)
                this.$router.push('/')
            }
        }
    }
</script>

<style scoped>

</style>