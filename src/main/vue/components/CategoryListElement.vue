<template>
    <v-expansion-panel>
        <v-expansion-panel-header>
            <v-text-field
                v-model="categoryName"
                class="headline"
                @focusin="$emit('text-input', true)"
                @focusout="$emit('text-input', false)"
            />
        </v-expansion-panel-header>
        <v-expansion-panel-content>
            <v-row>
                <v-col>
                    <v-btn @click="removeCategory(categoryID)">
                        <v-icon color="primary" left>mdi-delete</v-icon>
                        Kategorie
                    </v-btn>
                </v-col>
                <v-col>
                    <v-btn @click="createQuestion(categoryID)">
                        <v-icon color="primary" left>mdi-plus</v-icon>
                        Frage
                    </v-btn>
                </v-col>
            </v-row>
            <draggable v-model="questions">
                <v-list v-for="question in questions" :key="question.questionId">
                    <QuestionListElement
                        :poll-id="pollID"
                        :category-id="category.categoryId"
                        :question-id="question.questionId"
                        :question-message="question.questionMessage"
                        :question-type="question.questionType"
                    ></QuestionListElement>
                </v-list>
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
        categoryID: {
            type: Number,
        },
        pollID: {
            type: Number,
        },
    },
    computed: {
        ...mapGetters({ getCategory: 'pollOverview/getCategory' }),
        category() {
            return this.getCategory(this.categoryID)
        },
        categoryName: {
            get() {
                return this.getCategory(this.categoryID).categoryName
            },
            set(data) {
                this.setName(data)
            },
        },
        questions: {
            get() {
                return this.getCategory(this.categoryID).questionList
            },
            set(newList) {
                const payload = {
                    questions: newList,
                    categoryID: this.categoryID,
                }
                this.setQuestions(payload)
            },
        },
    },
    methods: {
        ...mapMutations({ setName: 'pollOverview/setCategoryName', setQuestions: 'pollOverview/updateQuestionOrder' }),
        ...mapActions({
            createQuestion: 'pollOverview/createQuestion',
            removeCategory: 'pollOverview/deleteCategory',
        }),
    },
}
</script>

<style scoped></style>
