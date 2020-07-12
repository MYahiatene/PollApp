<template>
    <v-card class="ma-1 pa-5">
        <h2>Konsistenzfragen von Umfrage: "{{ pollName }}"</h2>

        <v-expansion-panels flat v-model="panel">
            <v-expansion-panel>
                <v-expansion-panel-header>
                    Was sind eigentlich Konsistenzfragen?
                </v-expansion-panel-header>
                <v-expansion-panel-content>
                    <p>
                        Konsistenzfragen können eingebaut werden, um zu überprüfen, ob die Umfrage-Teilnehmer
                        tatsächlich konsistent auf die Umfragen antworten. Zum Beispiel würde man erwarten, dass eine
                        Person, die die Frage "Mögen sie gerne Kuchen?" mit "Ja" oder "Sehr Sogar" beantwortet hat, bei
                        der Frage "Welche dieser Lebensmittel mögen Sie?" unter anderem auch "Kuchen" auswählen wird.
                        Ist dies nicht der Fall, liegt der Verdacht nahe, dass die Umfrage nicht mit voller
                        Konzentration oder sogar zufällig ausgefüllt wurde, beziehungsweise, dass sich der
                        Umfragebearbeiter sehr unsicher in seiner eigenen Meinung ist. In allen Fällen kann es dann
                        sinnvoll sein, die betreffende Person von der weiteren Analyse auszuschließen.
                    </p>
                    <h4>Wie erstellt man Konsistenzfragen?</h4>

                    <p>
                        Hier können Sie Konsistenzfragenbeziehungen für die Umfrage "{{ pollName }}" erstellen. Dazu
                        wählen sie im linken Fenster eine Frage aus und entscheiden sich dann für eine Menge von
                        Antwortmöglichkeiten bei dieser Frage. Nachdem Sie das gleiche im rechten Fenster getan und auf
                        "Speichern" gedrückt haben, haben, werde diese zwei Mengen von Antwort-Möglichkeiten verknüpft.
                        Sobald ein Teilnehmer nun eine Antwort aus der linken Menge, aber keine aus der rechten wählt,
                        können seine Ergebnisse bei der Auswertung mit einem Klick ausgeschlossen werden.
                    </p>

                    <p>
                        Achtung! Handelt es sich um eine Auswahlfrage, bei der es erlaubt ist mehrere Antworten
                        auszuwählen, wird die Antwort-Konsistenz des Teilnehmer sofort geprüft, sobald er nur eine von
                        diesen ausgewählt hat.
                    </p>

                    <p>
                        Konsistenz innerhalb einer Frage: Sollten Sie bei einer Auswahlfrage zwei Antwortmöglichkeiten
                        eingebaut haben, die sie als gleichbedeutend ansehen, können sie auch dies mittels
                        Konsistenzfragen überprüfen. Dazu wählen Sie einfach rechts und links die gleiche Frage aus und
                        wählen danach jeweils eine der Antwortmöglichkeiten, die Ihrer Meinung nach von einem
                        aufmerksamen Teilnehmer nur gemeinsam gewählt werden können.
                    </p>

                    <v-btn text color="info" @click="panel = false" style="float: right;"> Ok! </v-btn>
                </v-expansion-panel-content>
            </v-expansion-panel>
        </v-expansion-panels>

        <v-card class="ma-1 pa-1" :style="frameColor">
            <v-row>
                <v-col cols="12" lg="6" md="6" sm="12">
                    <p>Verknüpfung zwischen:</p>
                    <v-card flat class="pa-2">
                        <v-overflow-btn
                            v-if="!askForCategory()"
                            prefix="Kategorie: "
                            :items="categoryTitles"
                            v-model="selectedCategory"
                            @input="updateCategoryIndex()"
                            elevation="0"
                            style="box-shadow: none;"
                        >
                        </v-overflow-btn>
                        <p v-else>Kategorie: "{{ selectedCategory }}"</p>
                        <v-spacer></v-spacer>
                        <v-overflow-btn
                            prefix="Frage: "
                            dense
                            :items="questionTitles"
                            v-model="selectedQuestion"
                            :no-data-text="'Keine Kategorie ausgewählt'"
                            @input="updateQuestionIndex()"
                            elevation="0"
                            style="box-shadow: none;"
                        >
                        </v-overflow-btn>
                        <v-select
                            prefix="Mögliche Antwort(en): "
                            :items="answerTitles"
                            v-model="selectedAnswers"
                            multiple
                            :no-data-text="'Keine Fragen ausgewählt'"
                            @input="updateAnswerIndices()"
                        >
                            <template v-slot:selection="{ item, index }">
                                <v-chip
                                    close
                                    @click:close="selectedAnswers.splice(index, 1)"
                                    v-if="index < answerTitlesDisplayedInSelect"
                                >
                                    <span>{{ item }}</span>
                                </v-chip>
                                <span v-if="index === answerTitlesDisplayedInSelect" class="grey--text caption"
                                    >(oder
                                    {{ selectedAnswers.length - answerTitlesDisplayedInSelect }}
                                    weitere)</span
                                >
                            </template>
                        </v-select>
                    </v-card>
                </v-col>

                <v-col cols="12" lg="6" md="6" sm="12">
                    <p>Und:</p>
                    <v-card flat class="pa-2">
                        <v-overflow-btn
                            v-if="!askForCategory()"
                            prefix="Kategorie: "
                            :items="categoryTitles"
                            v-model="selectedCategory2"
                            @input="updateCategoryIndex2()"
                            elevation="0"
                            style="box-shadow: none;"
                        >
                        </v-overflow-btn>
                        <p v-else>Kategorie: "{{ selectedCategory }}"</p>
                        <v-spacer></v-spacer>
                        <v-overflow-btn
                            prefix="Frage: "
                            dense
                            :items="questionTitles2"
                            v-model="selectedQuestion2"
                            :no-data-text="'Keine Kategorie ausgewählt'"
                            @input="updateQuestionIndex2()"
                            elevation="0"
                            style="box-shadow: none;"
                        >
                        </v-overflow-btn>
                        <v-select
                            prefix="Mögliche Antwort(en): "
                            :items="answerTitles2"
                            v-model="selectedAnswers2"
                            multiple
                            :no-data-text="'Keine Fragen ausgewählt'"
                            @input="updateAnswerIndices2()"
                        >
                            <template v-slot:selection="{ item, index }">
                                <v-chip
                                    close
                                    @click:close="selectedAnswers2.splice(index, 1)"
                                    v-if="index < answerTitlesDisplayedInSelect"
                                >
                                    <span>{{ item }}</span>
                                </v-chip>
                                <span v-if="index === answerTitlesDisplayedInSelect" class="grey--text caption"
                                    >(oder
                                    {{ selectedAnswers2.length - answerTitlesDisplayedInSelect }}
                                    weitere)</span
                                >
                            </template>
                        </v-select>
                    </v-card>
                </v-col>
            </v-row>
        </v-card>

        <v-card-actions>
            <!--            margin so the text doesn't touch the buttons -->
            <v-btn @click="addControlQuestion" class="mr-2" color="primary">
                Speichern
            </v-btn>
            {{ buttonInfo }}
            <v-btn @click="discardControlQuestion" class="ml-2" color="secondary">
                Änderungen verwerfen
            </v-btn>
        </v-card-actions>

        <v-dialog v-model="deleteDialog" max-width="290">
            <v-card>
                <v-card-title class="headline"> Sicher? </v-card-title>

                <v-card-text>
                    Wollen Sie diese Konsistenzfrage wirklich endgültig löschen?
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>

                    <v-btn color="error" @click="closeDeleteDialog(), addControlQuestion(currentId)">
                        Nein
                    </v-btn>

                    <v-btn color="success" @click="closeDeleteDialog(), deleteControlQuestion(currentId)">
                        Ja
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <v-card class="ma-1 pa-4">
            <h3>
                Bereits erstellte Konsistenzfragen:
            </h3>

            <div v-for="(cq, index) in listOfControlQuestions" :key="index">
                <v-row>
                    <v-chip @click="openCq(index)" class="ma-1" :outlined="index === currentId">
                        Konsistenzbeziehung {{ index + 1 }}: Zwischen {{ cq.a1.length }} Antwort(en) von Frage
                        {{ cq.q1 + 1 }} und {{ cq.a2.length }} Antwort(en) von Frage {{ cq.q2 }}

                        <v-icon class="ml-1" @click="copyQcQuestion(index)"> mdi-content-duplicate</v-icon>
                        <v-icon right @click="deleteDialog = true"> mdi-minus-circle-outline</v-icon>
                    </v-chip>

                    <div v-if="index === currentId">
                        <p style="font-style: italic;">(geöffnet)</p>
                    </div>
                </v-row>
            </div>
        </v-card>
    </v-card>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
    name: 'QAFilter',
    data() {
        return {
            categoryIndex: -1,
            questionIndex: -1,
            answerIndices: [],
            categoryIndex2: -1,
            questionIndex2: -1,
            answerIndices2: [],
            selectedCategory: '',
            selectedCategory2: '',
            selectedQuestion: '',
            selectedQuestion2: '',
            selectedAnswers: [],
            selectedAnswers2: [],
            answerTitlesDisplayedInSelect: 5,
            listOfControlQuestions: [],
            buttonInfo: '',
            currentId: -1,
            questionWasCopied: false,
            deleteDialog: false,
            panel: false,
        }
    },
    props: {
        pollIndex: {
            type: Number,
            default: 0,
        },
        filterId: {
            type: Number,
            default: 0,
        },
        initialCategoryIndex: {
            default: -1,
            type: Number,
        },
        initialQuestionIndex: {
            default: -1,
            type: Number,
        },
        initialAnswerIndices: {
            default: () => [],
            type: Array,
        },
    },
    mounted() {
        this.categoryIndex = this.initialCategoryIndex
        this.questionIndex = this.initialQuestionIndex
        this.answerIndices = this.initialAnswerIndices

        this.computeWordsFromIndices()
    },
    methods: {
        updateCategoryIndex() {
            this.categoryIndex = this.categoryTitles.indexOf(this.selectedCategory)
        },

        updateCategoryIndex2() {
            this.categoryIndex2 = this.categoryTitles.indexOf(this.selectedCategory2)
        },

        updateQuestionIndex() {
            this.questionIndex = this.questionTitles.indexOf(this.selectedQuestion)
        },

        updateQuestionIndex2() {
            this.questionIndex2 = this.questionTitles2.indexOf(this.selectedQuestion2)
        },

        updateAnswerIndices() {
            for (let i = 0; i < this.selectedAnswers.length; i++) {
                this.answerIndices[i] = this.answerTitles.indexOf(this.selectedAnswers[i])
            }
        },

        updateAnswerIndices2() {
            console.log('update Answers 2 ')
            for (let i = 0; i < this.selectedAnswers2.length; i++) {
                this.answerIndices2[i] = this.answerTitles2.indexOf(this.selectedAnswers2[i])
            }
            console.log(this.answerIndices2)
        },

        askForCategory() {
            const OnlyOneCategory = this.categories.length === 1
            if (OnlyOneCategory) {
                this.selectedCategory = this.categoryTitles[0]
                this.selectedCategory2 = this.categoryTitles[0]
                this.updateCategoryIndex()
                this.updateCategoryIndex2()
            }
            return OnlyOneCategory
        },

        addControlQuestion() {
            console.log('add')
            if (
                this.categoryIndex === -1 ||
                this.questionIndex === -1 ||
                this.answerIndices.length === 0 ||
                this.categoryIndex2 === -1 ||
                this.questionIndex2 === -1 ||
                this.answerIndices2.length === 0
            ) {
                this.buttonInfo = 'Bitte füllen Sie erst alle Felder aus!'
            } else {
                const ans1 = []
                for (let i = 0; i < this.answerIndices.length; i++) {
                    ans1[i] = this.answerIndices[i]
                }

                const ans2 = []
                for (let i = 0; i < this.answerIndices2.length; i++) {
                    ans2[i] = this.answerIndices2[i]
                }

                if (this.currentId === -1) {
                    this.listOfControlQuestions.push({
                        c1: this.categoryIndex,
                        q1: this.questionIndex,
                        a1: ans1,
                        c2: this.categoryIndex2,
                        q2: this.questionIndex2,
                        a2: ans2,
                    })
                } else {
                    this.listOfControlQuestions[this.currentId] = {
                        c1: this.categoryIndex,
                        q1: this.questionIndex,
                        a1: ans1,
                        c2: this.categoryIndex2,
                        q2: this.questionIndex2,
                        a2: ans2,
                    }
                }

                this.buttonInfo = ''
                this.currentId = -1
                this.categoryIndex = -1
                this.questionIndex = -1
                this.answerIndices = []
                this.categoryIndex2 = -1
                this.questionIndex2 = -1
                this.answerIndices2 = []
                this.computeWordsFromIndices()
            }
        },

        discardControlQuestion() {
            // deletes the CQ that is currently edited from the editors window, but still keeps her former version.
            this.buttonInfo = ''
            this.currentId = -1
            this.categoryIndex = -1
            this.questionIndex = -1
            this.answerIndices = []
            this.categoryIndex2 = -1
            this.questionIndex2 = -1
            this.answerIndices2 = []
            this.computeWordsFromIndices()
        },

        deleteControlQuestion(index) {
            // the question is added here, so its no longer visble in the editing component
            this.addControlQuestion(index)
            if (index > -1) {
                this.listOfControlQuestions.splice(index, 1)
            }
        },

        openCq(index) {
            if (this.questionWasCopied) {
                this.currentId = -1
                this.questionWasCopied = false
            } else {
                this.currentId = index
            }

            this.categoryIndex = this.listOfControlQuestions[index].c1
            this.questionIndex = this.listOfControlQuestions[index].q1

            this.answerIndices = []

            for (let i = 0; i < this.listOfControlQuestions[index].a1.length; i++) {
                this.answerIndices[i] = this.listOfControlQuestions[index].a1[i]
            }
            this.categoryIndex2 = this.listOfControlQuestions[index].c2
            this.questionIndex2 = this.listOfControlQuestions[index].q2

            this.answerIndices2 = []

            for (let i = 0; i < this.listOfControlQuestions[index].a2.length; i++) {
                this.answerIndices2[i] = this.listOfControlQuestions[index].a2[i]
            }

            console.log('open')

            console.log('a1: ')
            console.log(this.listOfControlQuestions[index].a1)

            console.log('answerIndices: ')
            console.log(this.answerIndices)

            this.computeWordsFromIndices()
        },

        computeWordsFromIndices() {
            this.selectedCategory = this.categoryTitles[this.categoryIndex]
            this.selectedQuestion = this.questionTitles[this.questionIndex]
            this.selectedAnswers = []
            for (let i = 0; i < this.answerIndices.length; i++) {
                this.selectedAnswers[i] = this.answerTitles[this.answerIndices[i]]
            }

            this.selectedCategory2 = this.categoryTitles[this.categoryIndex2]
            this.selectedQuestion2 = this.questionTitles2[this.questionIndex2]
            this.selectedAnswers2 = []
            for (let i = 0; i < this.answerIndices2.length; i++) {
                this.selectedAnswers2[i] = this.answerTitles2[this.answerIndices2[i]]
            }

            this.$forceUpdate()
        },

        copyQcQuestion() {
            this.questionWasCopied = true
        },

        closeDeleteDialog() {
            this.deleteDialog = false
        },
    },
    computed: {
        ...mapGetters({
            polls: 'evaluation/getPolls',
        }),

        pollName() {
            return this.polls[0].pollName
        },

        pollTitles() {
            const pollTitles = Object.assign([{}], this.polls)
            for (let i = 0; i < pollTitles.length; i++) {
                pollTitles[i] = this.polls[i].pollName
            }
            return pollTitles
        },
        categoryTitles() {
            const l = []
            for (let i = 0; i < this.polls[this.pollIndex].categoryList.length; i++) {
                l[i] = this.polls[this.pollIndex].categoryList[i].categoryName
            }
            return l
        },

        categories() {
            return this.polls[this.pollIndex].categoryList
        },

        questionTitles() {
            if (this.categoryIndex === -1) {
                return []
            } else {
                const l = []
                for (let i = 0; i < this.categories[this.categoryIndex].questionList.length; i++) {
                    l[i] = this.categories[this.categoryIndex].questionList[i].questionMessage
                }
                return l
            }
        },

        questionTitles2() {
            if (this.categoryIndex2 === -1) {
                return []
            } else {
                const l = []
                for (let i = 0; i < this.categories[this.categoryIndex2].questionList.length; i++) {
                    l[i] = this.categories[this.categoryIndex2].questionList[i].questionMessage
                }
                return l
            }
        },

        questions() {
            if (this.categoryIndex === -1) {
                return []
            } else {
                return this.categories[this.categoryIndex].questionList
            }
        },

        questions2() {
            if (this.categoryIndex2 === -1) {
                return []
            } else {
                return this.categories[this.categoryIndex2].questionList
            }
        },

        answerTitles() {
            if (this.questionIndex === -1) {
                return []
            } else {
                const l = []
                for (let i = 0; i < this.questions[this.questionIndex].answerPossibilities.length; i++) {
                    l[i] = this.questions[this.questionIndex].answerPossibilities[i]
                }

                return l
            }
        },

        answerTitles2() {
            if (this.questionIndex2 === -1) {
                return []
            } else {
                const l = []
                for (let i = 0; i < this.questions2[this.questionIndex2].answerPossibilities.length; i++) {
                    l[i] = this.questions2[this.questionIndex2].answerPossibilities[i]
                }

                return l
            }
        },

        // this needs to be computed, so we can get it from the nuxt Config

        frameColor() {
            return 'background-color:' + this.$vuetify.theme.currentTheme.background2 + ';'
        },
    },
}
</script>

<style scoped></style>
