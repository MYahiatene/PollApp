<template>
    <b-container>
        <b-row>
            <b-col>
                <b-card header="Artikel erstellen" class="mt-3">
                    <b-form @submit.prevent="save">
                        <b-form-group label="Titel:" label-cols="3">
                            <b-form-input type="text" v-model="article.title"></b-form-input>
                        </b-form-group>
                        <b-form-group label="Text:" label-cols="3">
                            <b-form-textarea type="textarea" rows="10" v-model="article.text"></b-form-textarea>
                        </b-form-group>
                        <button type="submit">Speichern</button>
                    </b-form>
                </b-card>
            </b-col>
        </b-row>
    </b-container>
</template>

<script>
    import {mapMutations} from "vuex";

    export default {
        name: "Create",
        data() {
            return {
                article: {
                    title: "",
                    text: ""
                }
            }
        },
        methods: {
            ...mapMutations([
                'saveArticle'
            ]),
            save() {
                this.saveArticle(this.article)
                this.$router.push('/')
            }
        }
    }
</script>

<style scoped>

</style>