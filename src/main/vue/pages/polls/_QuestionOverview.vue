<template>
    <AuthGate v-if="isAuthenticated !== true"></AuthGate>
    <v-container v-else-if="storeValid">
        <v-card class="pa-2 ma-0">
            <v-text-field v-model="pollData.pollName" clearable class="display-1" />
        </v-card>
        <v-row>
            <v-col cols="12" lg="4" md="4" sm="4">
                <v-row>
                    <v-btn depressed @click="createCategory()" class="ma-5">
                        <v-icon color="primary">
                            mdi-plus
                        </v-icon>
                        Kategorie hinzufügen
                    </v-btn>
                    <v-spacer></v-spacer>
                    <v-btn depressed @click="createQuestion()" class="ma-5">
                        <v-icon color="primary">
                            mdi-plus
                        </v-icon>
                        Frage hinzufügen
                    </v-btn>

                    <v-card v-for="category in categoryData" :key="category.categoryId" class="ma-5" width="600">
                        <v-card-title>
                            <h2 style="font-weight: normal;" class="ma-0">
                                {{ category.categoryName }}
                            </h2>
                            <v-spacer></v-spacer>
                            <template>
                                <div class="text-center">
                                    <v-dialog v-model="categoryDialog" max-width="500">
                                        <template v-slot:activator="{ on }">
                                            <v-icon medium class="mr-2" v-on="on" @click="editCat(category)">
                                                mdi-pencil
                                            </v-icon>
                                        </template>
                                        <v-card-title>
                                            <span class="headline">Kategorie umbennen</span>
                                        </v-card-title>
                                        <v-row cols="12" sm="6" md="4">
                                            <v-text-field v-model="editCategory.categoryName"></v-text-field>
                                        </v-row>
                                        <v-card-actions>
                                            <v-btn color="blue darken-1" text @click="close">Abbrechen</v-btn>
                                            <v-spacer></v-spacer>
                                            <v-btn color="blue darken-1" text @click="save">Sichern</v-btn>
                                        </v-card-actions>
                                    </v-dialog>
                                </div>
                            </template>
                            <v-icon medium @click="deleteCategory(category)">
                                mdi-delete
                            </v-icon>
                        </v-card-title>

                        <v-divider></v-divider>

                        <v-list>
                            <v-list-item v-for="question in category.questionList" :key="question.questionId">
                                <v-list-item-title>{{ question.questionMessage }}</v-list-item-title>
                            </v-list-item>
                        </v-list>

                        <!--
                        <v-card-text>
                            <v-row>
                                <v-col cols="12" lg="12" md="12" sm="12">
                                    <v-expansion-panels tile multiple :disabled="disableDrag" class="mt-n6">
                                        <v-list two-line>

                                        </v-list>
                                    </v-expansion-panels>
                                </v-col>
                            </v-row>
                        </v-card-text>
                -->
                    </v-card>
                </v-row>
            </v-col>

            <v-col cols="12" lg="8" md="8" sm="8">
                <v-card class="pa-1" :style="frameColor">
                    <QuestionBuildWidget></QuestionBuildWidget>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
    <v-container v-else>
        <v-card>
            <v-card-title>
                <v-spacer />
                <v-icon>
                    mdi-sync-alert
                </v-icon>
                Der Umfrato-Server hat noch nicht geantwortet
                <v-spacer />
            </v-card-title>
        </v-card>
    </v-container>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
// import draggable from 'vuedraggable'
import QuestionBuildWidget from '../../components/QuestionBuildWidget'
// import CategoryListElement from '../../components/CategoryListElement'

export default {
    name: 'QuestionOverview',
    components: { /* CategoryListElement, */ QuestionBuildWidget /* draggable */ },
    data() {
        return {
            editedIndex: -1,
            disableDrag: false,
            pollData: [],
            categoryData: [],
            questionListData: [],
            defaultCategory: {
                categoryId: null,
                categoryName: '',
                pollId: null,
                questionList: [],
            },
            editCategory: {
                categoryId: null,
                categoryName: 'Neue Kategorie',
                pollId: this.$route.params.QuestionOverview,
                questionList: [],
            },
            categoryDialog: false,
        }
    },
    created() {
        this.loadPoll()
        this.loadCategories()
    },
    computed: {
        ...mapGetters({
            isAuthenticated: 'login/isAuthenticated',
            getCategories: 'pollOverview/getCategory',
        }),
        pollId() {
            return parseInt(this.$route.params.QuestionOverview)
        },
        pollname: {
            get() {
                return this.pollData.pollName
            },
            set(data) {
                this.setPollName(data)
            },
        },
        storeValid() {
            return this.pollId !== undefined
        },
        /** updateQuestionList() {
            return this.$store.state.questionOverview.questionText
        }, */

        // this needs to be computed, so we can get it from the nuxt Config

        frameColor() {
            return 'background-color:' + this.$vuetify.theme.currentTheme.softAccent + ';'
        },
    },
    methods: {
        pollName() {
            return this.pollData.pollName
        },
        async loadPoll() {
            await this.$axios
                .get('/getonepoll', {
                    params: {
                        pollId: this.$route.params.QuestionOverview,
                    },
                })
                .then((response) => {
                    this.pollData = response.data
                })
                .catch((error) => {
                    console.log(error)
                })
        },
        async loadCategories() {
            await this.$axios
                .get('/getallcategories', {
                    params: {
                        pollId: this.$route.params.QuestionOverview,
                    },
                })
                .then((response) => {
                    this.categoryData = response.data
                    this.categoryData.forEach((ele) => {
                        this.questionListData.push(ele.questionList)
                    })
                })
                .catch((error) => {
                    console.log(error)
                })
        },
        createCategory() {
            this.$axios
                .post('/addcategory', {
                    pollId: this.$route.params.QuestionOverview,
                    name: this.editCategory.categoryName,
                })
                .then((response) => {
                    this.editCategory = response.data
                    console.log(this.editCategory)
                    this.defaultCategory = Object.assign({}, this.editCategory)
                    this.categoryData.push(this.defaultCategory)
                    console.log(this.categoryData)
                })
        },
        editCat(category) {
            this.editedIndex = this.categoryData.indexOf(category)
            this.editCategory = Object.assign({}, category)
            this.categoryDialog = true
        },
        editCategoryData(categoryID, categoryName) {
            this.$axios.put('/editcategory', {
                categoryId: categoryID,
                name: categoryName,
            })
        },
        save() {
            Object.assign(this.categoryData[this.editedIndex], this.editCategory)
            this.editCategoryData(this.editCategory.categoryId, this.editCategory.categoryName)
            this.close()
        },
        close() {
            this.categoryDialog = false
            this.$nextTick(() => {
                this.editCategory = Object.assign({}, this.defaultCategory)
                this.editedIndex = -1
            })
        },
        deleteCategory(category) {
            const index = this.categoryData.indexOf(category)
            confirm('Sind sie sich sicher, dass sie diese Kategorie löschen möchten?') &&
                this.categoryData.splice(index, 1) &&
                this.$axios
                    .put('/deletecategory', {
                        categoryId: category.categoryId,
                    })
                    .catch((error) => {
                        console.log(error)
                    })
        },
        ...mapActions({
            sendData: 'pollOverview/sendData',
            setCategory: 'pollOverview/setCategory',
        }),
        ...mapMutations({
            saveData: 'pollOverview/saveData',
            setPollName: 'pollOverview/setPollName',
        }),
        saveState() {
            const payload = {
                pollId: '',
                questionMessage: this.test,
                answerPossibilities: [],
                questionType: '',
            }
            this.saveData(payload)
        },
        sendState() {
            this.sendData()
        },
        disableDraggable(disable) {
            this.disableDrag = disable
        },
    },
}
</script>
