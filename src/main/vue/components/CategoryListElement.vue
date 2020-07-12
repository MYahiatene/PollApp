<template>
    <div>
        <v-expansion-panel style="min-width: 400px; max-width: 400px;">
            <v-expansion-panel-header class="my-0" min-width="400" :style="headerColor">
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
                        <draggable :group="{ name: 'g1' }">
                            <h2 slot="header" style="font-weight: normal;">
                                {{ category.categoryName.substr(0, 27) }}
                            </h2>
                            <v-spacer></v-spacer>
                        </draggable>
                    </div>
                </template>
            </v-expansion-panel-header>
            <v-expansion-panel-content :style="backgroundColor">
                <v-row justify="center">
                    <v-col>
                        <v-dialog v-model="dialog" persistent max-width="290">
                            <template v-slot:activator="{ on, attrs }">
                                <v-btn v-bind="attrs" v-on="on">
                                    <v-icon color="primary" left>mdi-delete</v-icon>
                                    Kategorie
                                </v-btn>
                            </template>
                            <v-card v-if="category.questionList.length">
                                <v-card-title class="headline">Warnung</v-card-title>
                                <v-card-text
                                    >In der Kategorie befinden sich noch Fragen. Fragen mitlöschen?
                                </v-card-text>
                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="green darken-1" text @click="dialog = false">Abbrechen</v-btn>
                                    <v-btn color="green darken-1" text @click="setDeleteIndex(category, '1')"
                                        >Nein
                                    </v-btn>
                                    <v-btn color="green darken-1" text @click="setDeleteIndex(category, '2')">Ja</v-btn>
                                </v-card-actions>
                            </v-card>
                            <v-card v-else>
                                <v-card-title class="headline">Warnung</v-card-title>
                                <v-card-text>Sind Sie sicher, dass die Kategorie gelöscht werden soll?</v-card-text>
                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="green darken-1" text @click="dialog = false">Nein</v-btn>
                                    <v-btn color="green darken-1" text @click="deleteCategory(category)">Ja</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>
                    </v-col>
                </v-row>
                <v-row></v-row>
                <draggable @end="onEnd" :move="onMove" :list="questions" :group="{ name: 'g1' }">
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
                </draggable>
                <p v-if="questions.length === 0">Es wurden noch keine Fragen in dieser Kategorie erstellt.</p>
            </v-expansion-panel-content>
        </v-expansion-panel>
    </div>
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
        categoryData: {
            type: Array,
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
        categoryNames: {
            type: Array,
        },
    },
    data() {
        return {
            draggedQuestionId: -1,
            dialog: false,
            deleteIndex: '0',
        }
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
        onMove(evt) {
            this.draggedQuestionId = evt.draggedContext.element.questionId
            console.log(this.draggedQuestionId)
        },
        onEnd(evt) {
            const oldIndex2 = evt.oldIndex
            const newIndex2 = evt.newIndex
            let newCategoryId2 = null
            this.categoryData.forEach((c) => {
                c.questionList.forEach((question) => {
                    if (question.questionId === this.draggedQuestionId) {
                        newCategoryId2 = c.categoryId
                    }
                })
            })
            console.log('oldIndex: ' + oldIndex2)
            console.log('newIndex: ' + newIndex2)
            console.log('newCategoryId2: ' + newCategoryId2)
            console.log('QuestionId: ' + this.draggedQuestionId)

            this.$axios.post('/changeQuestionIndex', {
                newIndex: newIndex2,
                newCategoryId: newCategoryId2,
                questionId: this.draggedQuestionId,
            })
        },
        setDeleteIndex(category, deleteIndex) {
            this.deleteIndex = deleteIndex
            this.dialog = false
            this.deleteCategory(category)
        },
        editCat(category) {
            this.$axios.put('/editcategory', {
                categoryId: category.categoryId,
                categoryName: category.categoryName,
            })
            this.$emit('text-input', false)
            const index = this.categoryData.indexOf(category)
            this.categoryNames[index].text = category.categoryName
        },
        deleteCategory(category) {
            const index = this.categoryData.indexOf(category)
            if (!index) {
                alert('Löschen nicht möglich, da es die Standardkategorie geben muss')
            } else {
                console.log('DeleteIndexSetted')
                console.log(this.deleteIndex)
                this.categoryData.splice(index, 1)
                this.categoryNames.splice(index, 1)
                this.$axios
                    .put('/deletecategory', {
                        categoryId: category.categoryId,
                        questionState: this.deleteIndex,
                    })
                    .catch((error) => {
                        console.log(error)
                    })
                if (this.deleteIndex === '1') {
                    console.log(this.categoryData)
                    category.questionList.forEach((question) => {
                        question.categoryId = this.categoryData[0].categoryId
                        this.categoryData[0].questionList.push(question)
                    })
                }
                this.deleteIndex = '0'
            }
        },
        ...mapMutations({
            setName: 'pollOverview/setCategoryName',
            setQuestions: 'pollOverview/updateQuestionOrder',
        }),
        ...mapActions({
            removeCategory: 'pollOverview/deleteCategory',
        }),
    },
}
</script>

<style scoped></style>
