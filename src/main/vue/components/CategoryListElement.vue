<template>
    <v-expansion-panel>
        <v-expansion-panel-header class="my-0" :style="headerColor">
            <template v-slot:default="{ open }">
                <div v-if="open">
                    <!--                    In class we apply negative margin, so that the header is dense-->
                    <v-text-field
                        v-model="category.categoryName"
                        class="headline my-n4"
                        placeholder="Name der Kategorie"
                        @focusin="$emit('text-input', true)"
                        @focusout="editCat(category)"
                    />
                </div>
                <div v-else>
                    <h2 style="font-weight: normal;">{{ category.categoryName }}</h2>
                    <v-spacer></v-spacer>
                </div>
            </template>
        </v-expansion-panel-header>
        <v-expansion-panel-content :style="backgroundColor">
            <v-row>
                <v-col>
                    <v-btn depressed @click="checkCategoryList()">
                        <v-icon color="primary" left>mdi-delete</v-icon>
                        Kategorie
                    </v-btn>
                </v-col>
            </v-row>
            <draggable v-model="questions">
                <v-list v-for="question in questions" :key="question.questionId" :style="backgroundColor">
                    <QuestionListElement
                        :pollData="pollData"
                        :poll-id="pollID"
                        :category-id="categoryID"
                        :question="question"
                        :category="category"
                    ></QuestionListElement>
                    <v-spacer></v-spacer>
                    <v-spacer></v-spacer>
                </v-list>
                <p v-if="questions.length === 0">Es wurden noch keine Fragen in dieser Kategorie erstellt.</p>
            </draggable>
        </v-expansion-panel-content>
    </v-expansion-panel>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import draggable from 'vuedraggable'
import QuestionListElement from './QuestionListElement'

export default {
    name: 'CategoryListElement',
    components: { QuestionListElement, draggable },
    props: {
        pollData: {
            type: Object,
        },
        categoryID: {
            type: Number,
        },
        pollID: {
            type: Number,
        },
        categoryName: {
            type: String,
        },
        questions: {
            type: Array,
        },
        buildIndex: { type: Number },
        category: { type: Object },
    },
    computed: {
        ...mapGetters({ getCategory: 'pollOverview/getCategory', getPolls: 'navigation/getPolls' }),

        // this needs to be computed, so we can get it from the nuxt Config

        backgroundColor() {
            return 'background-color:' + this.$vuetify.theme.currentTheme.background2 + ';'
        },

        headerColor() {
            return 'background-color:' + this.$vuetify.theme.currentTheme.header + ';'
        },
    },
    methods: {
        editCat(category) {
            this.$axios.put('/editcategory', { categoryId: category.categoryId, categoryName: category.categoryName })
            this.$emit('text-input', false)
        },
        checkCategoryList() {
            const polls = this.getPolls
            polls.forEach((poll) => {
                if (poll.pollId === this.pollID) {
                    console.log(poll.categoryList)
                    if (poll.categoryList.length <= 1) {
                        alert('Löschen nicht möglich')
                    } else {
                        this.removeCategory(this.categoryID)
                    }
                }
            })
        },
        ...mapMutations({ setName: 'pollOverview/setCategoryName', setQuestions: 'pollOverview/updateQuestionOrder' }),
        ...mapActions({
            removeCategory: 'pollOverview/deleteCategory',
        }),
    },
}
</script>

<style scoped></style>
