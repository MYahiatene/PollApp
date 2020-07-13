<template>
    <!--    Here the user can create consistency relationships between two questions of a poll-->
    <v-dialog v-model="dialog" overlay-color="background" persistent width="500" overlay-opacity="0.95" fullscreen>
        <template #activator="{ on: dialog }">
            <v-btn v-if="useEvaluationStore" v-on="{ ...dialog }">
                Konsistenzfragen bearbeiten
            </v-btn>
            <v-tooltip v-else bottom>
                <template #activator="{ on: tooltip }">
                    <v-btn class="mr-n5" icon v-on="{ ...tooltip, ...dialog }" @click="initialize">
                        <v-icon large color="primary">
                            mdi-lock-pattern
                        </v-icon>
                    </v-btn>
                </template>
                <span>Konsistenzfragen bearbeiten</span>
            </v-tooltip>
        </template>
        <v-card class="ma-1 pa-5" v-if="pollName !== ''">
            <v-row>
                <v-col cols="12" lg="11">
                    <h2>Konsistenzfragen von Umfrage: "{{ pollName }}"</h2>
                </v-col>
                <v-col cols="12" lg="1">
                    <v-btn color="primary" @click=";(dialog = false), $emit('close-event')">
                        Fertig
                    </v-btn>
                </v-col>
            </v-row>

            <!--        This provides the user with an explanation of how the consistency Questions work -->
            <v-expansion-panels v-model="panel" flat>
                <v-expansion-panel>
                    <v-expansion-panel-header>
                        Was sind eigentlich Konsistenzfragen?
                    </v-expansion-panel-header>
                    <v-expansion-panel-content>
                        <p>
                            Konsistenzfragen können eingebaut werden, um zu überprüfen, ob die Umfrage-Teilnehmer
                            tatsächlich konsistent auf die Umfragen antworten. Zum Beispiel würde man erwarten, dass
                            eine Person, die die Frage "Mögen sie gerne Kuchen?" mit "Ja" oder "Sehr Sogar" beantwortet
                            hat, bei der Frage "Welche dieser Lebensmittel mögen Sie?" unter anderem auch "Kuchen"
                            auswählen wird. Ist dies nicht der Fall, liegt der Verdacht nahe, dass die Umfrage nicht mit
                            voller Konzentration oder sogar zufällig ausgefüllt wurde, beziehungsweise, dass sich der
                            Umfragebearbeiter sehr unsicher in seiner eigenen Meinung ist. In allen Fällen kann es dann
                            sinnvoll sein, die betreffende Person von der weiteren Analyse auszuschließen.
                        </p>
                        <h4>Wie erstellt man Konsistenzfragen?</h4>

                        <p>
                            Hier können Sie Konsistenzfragenbeziehungen für die Umfrage "{{ pollName }}" erstellen. Dazu
                            wählen sie im linken Fenster eine Frage aus und entscheiden sich dann für eine Menge von
                            Antwortmöglichkeiten bei dieser Frage. Achtung, Sie haben dabei nur die Auswahl aus Range-,
                            Slider- und Auswahlfragen. Nachdem Sie das gleiche im rechten Fenster getan und auf
                            "Speichern" gedrückt haben, haben, werde diese zwei Mengen von Antwort-Möglichkeiten
                            verknüpft. Sobald ein Teilnehmer nun eine Antwort aus der linken Menge, aber keine aus der
                            rechten wählt, können seine Ergebnisse bei der Auswertung mit einem Klick ausgeschlossen
                            werden.
                        </p>

                        <p>
                            Achtung! Handelt es sich um eine Auswahlfrage, bei der es erlaubt ist mehrere Antworten
                            auszuwählen, wird die Antwort-Konsistenz des Teilnehmer sofort geprüft, sobald er nur eine
                            von diesen ausgewählt hat.
                        </p>

                        <p>
                            Konsistenz innerhalb einer Frage: Sollten Sie bei einer Auswahlfrage zwei
                            Antwortmöglichkeiten eingebaut haben, die sie als gleichbedeutend ansehen, können sie auch
                            dies mittels Konsistenzfragen überprüfen. Dazu wählen Sie einfach rechts und links die
                            gleiche Frage aus und wählen danach jeweils eine der Antwortmöglichkeiten, die Ihrer Meinung
                            nach von einem aufmerksamen Teilnehmer nur gemeinsam gewählt werden können.
                        </p>

                        <v-btn text color="info" style="float: right;" @click="panel = false"> Ok! </v-btn>
                    </v-expansion-panel-content>
                </v-expansion-panel>
            </v-expansion-panels>

            <!--        The contend consists of two basically identical side, one to specify the first question and one for the second-->

            <v-card class="ma-1 pa-1" :style="frameColor">
                <v-row>
                    <v-col cols="12" lg="6" md="6" sm="12">
                        <p>Verknüpfung zwischen:</p>
                        <v-card flat class="pa-2">
                            <!--                        pick category for first question-->
                            <v-overflow-btn
                                v-if="!askForCategory()"
                                v-model="selectedCategory"
                                prefix="Kategorie: "
                                :items="categoryTitles"
                                elevation="0"
                                style="box-shadow: none;"
                                @input="updateCategoryIndex()"
                            >
                            </v-overflow-btn>
                            <p v-else>Kategorie: "{{ selectedCategory }}"</p>
                            <v-spacer></v-spacer>
                            <!--                            pick question for first question-->
                            <v-overflow-btn
                                v-model="selectedQuestion"
                                prefix="Frage: "
                                dense
                                :items="questionTitles"
                                :no-data-text="
                                    selectedCategory === ''
                                        ? 'Keine Kategorie ausgewählt'
                                        : 'Keine nutzbaren Fragen in dieser Kategorie. Bitte wählen Sie eine andere!'
                                "
                                elevation="0"
                                style="box-shadow: none;"
                                @input="updateQuestionIndex()"
                            >
                            </v-overflow-btn>

                            <!--                            pick answer for first answer-->

                            <v-select
                                v-if="
                                    questions[questionIndex] &&
                                    !(questions[questionIndex].questionType === 'SliderQuestion')
                                "
                                v-model="selectedAnswers"
                                prefix="Mögliche Antwort(en): "
                                :items="answerTitles"
                                multiple
                                :no-data-text="'Keine Fragen ausgewählt'"
                                @change="updateAnswerIndices()"
                            >
                                <!--                            if there are a lot of selected answers only some are displayed-->
                                <template v-slot:selection="{ item, index }">
                                    <v-chip
                                        v-if="index < answerTitlesDisplayedInSelect"
                                        close
                                        @click:close="selectedAnswers.splice(index, 1)"
                                    >
                                        <span>{{ item }}</span>
                                    </v-chip>
                                    <span v-if="index === answerTitlesDisplayedInSelect" class="grey--text caption"
                                        >(oder
                                        {{ selectedAnswers.length - answerTitlesDisplayedInSelect }}
                                        weitere)
                                    </span>
                                </template>
                            </v-select>

                            <!--                            if we have a slider question-->
                            <div
                                v-if="
                                    questions[questionIndex] &&
                                    questions[questionIndex].questionType === 'SliderQuestion'
                                "
                            >
                                Antwort-Bereich:
                                <v-range-slider
                                    v-model="range"
                                    :min="questions[questionIndex].startValue"
                                    :max="questions[questionIndex].endValue"
                                    :step="questions[questionIndex].stepSize"
                                    :thumb-label="true"
                                >
                                    <template v-slot:prepend>
                                        <v-text-field
                                            :value="range[0]"
                                            class="mt-0 pt-0"
                                            hide-details
                                            single-line
                                            type="number"
                                            style="width: 60px;"
                                            @change="$set(range, 0, $event)"
                                        ></v-text-field>
                                    </template>
                                    <template v-slot:append>
                                        <v-text-field
                                            :value="range[1]"
                                            class="mt-0 pt-0"
                                            hide-details
                                            single-line
                                            type="number"
                                            style="width: 60px;"
                                            @change="$set(range, 1, $event)"
                                        ></v-text-field>
                                    </template>
                                </v-range-slider>
                            </div>
                        </v-card>
                    </v-col>

                    <v-col cols="12" lg="6" md="6" sm="12">
                        <p>Und:</p>
                        <v-card flat class="pa-2">
                            <!--                            pick category for second question-->
                            <v-overflow-btn
                                v-if="!askForCategory()"
                                v-model="selectedCategory2"
                                prefix="Kategorie: "
                                :items="categoryTitles"
                                elevation="0"
                                style="box-shadow: none;"
                                @input="updateCategoryIndex2()"
                            >
                            </v-overflow-btn>
                            <p v-else>Kategorie: "{{ selectedCategory }}"</p>
                            <v-spacer></v-spacer>
                            <!--                            pick question for second question-->
                            <v-overflow-btn
                                v-model="selectedQuestion2"
                                prefix="Frage: "
                                dense
                                :items="questionTitles2"
                                :no-data-text="
                                    selectedCategory === ''
                                        ? 'Keine Kategorie ausgewählt'
                                        : 'Keine nutzbaren Fragen in dieser Kategorie. Bitte wählen Sie eine andere!'
                                "
                                elevation="0"
                                style="box-shadow: none;"
                                @input="updateQuestionIndex2()"
                            >
                            </v-overflow-btn>
                            <!--                            pick answers for second questions-->
                            <v-select
                                v-if="
                                    questions2[questionIndex2] &&
                                    !(questions2[questionIndex2].questionType === 'SliderQuestion')
                                "
                                v-model="selectedAnswers2"
                                prefix="Mögliche Antwort(en): "
                                :items="answerTitles2"
                                multiple
                                :no-data-text="'Keine Fragen ausgewählt'"
                                @change="updateAnswerIndices2()"
                            >
                                <!--                                if there are too many answers only a few are displayed-->
                                <template v-slot:selection="{ item, index }">
                                    <v-chip
                                        v-if="index < answerTitlesDisplayedInSelect"
                                        close
                                        @click:close="selectedAnswers2.splice(index, 1)"
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
                            <div
                                v-if="
                                    questions2[questionIndex2] &&
                                    questions2[questionIndex2].questionType === 'SliderQuestion'
                                "
                            >
                                <!--                                if a slider question is picked-->
                                Antwort-Bereich:
                                <v-range-slider
                                    v-model="range2"
                                    :min="questions2[questionIndex2].startValue"
                                    :max="questions2[questionIndex2].endValue"
                                    :step="questions2[questionIndex2].stepSize"
                                    :thumb-label="true"
                                >
                                    <template v-slot:prepend>
                                        <v-text-field
                                            :value="range2[0]"
                                            class="mt-0 pt-0"
                                            hide-details
                                            single-line
                                            type="number"
                                            style="width: 60px;"
                                            @change="$set(range2, 0, $event)"
                                        ></v-text-field>
                                    </template>
                                    <template v-slot:append>
                                        <v-text-field
                                            :value="range2[1]"
                                            class="mt-0 pt-0"
                                            hide-details
                                            single-line
                                            type="number"
                                            style="width: 60px;"
                                            @change="$set(range2, 1, $event)"
                                        ></v-text-field>
                                    </template>
                                </v-range-slider>
                            </div>
                        </v-card>
                    </v-col>
                </v-row>
            </v-card>

            <!--            buttons for saving or discarding changes-->

            <v-card-actions>
                <!--            margin so the text doesn't touch the buttons -->
                <v-btn class="mr-2" color="primary" @click="addControlQuestion">
                    Speichern
                </v-btn>
                {{ buttonInfo }}
                <v-btn class="ml-2" color="secondary" @click="discardControlQuestion">
                    Änderungen verwerfen
                </v-btn>
            </v-card-actions>

            <!--        the dialog is opened to confirm that the question should be deleted-->

            <v-dialog v-model="deleteDialog" max-width="290">
                <v-card>
                    <v-card-title class="headline"> Sicher? </v-card-title>

                    <v-card-text>
                        Wollen Sie diese Konsistenzfrage wirklich entgültig löschen?
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

            <!--        in this v-card the consistency relationships that are already created are listed-->

            <v-card class="ma-1 pa-4">
                <h3>
                    Bereits erstellte Konsistenzfragen:
                </h3>

                <div v-for="(cq, index) in listOfControlQuestions" :key="index">
                    <v-row>
                        <v-chip class="ma-1" :outlined="index === currentId" @click="openCq(index)">
                            {{ index }}: Kat {{ cq.c1 + 1 }} / Frage {{ cq.q1 + 1 }}:
                            {{(cq.s1? "Intervall" :  cq.a1.length + " Antwort(en) ")}} mit Kat {{ cq.c2 + 1 }} / Frage
                            {{ cq.q2 + 1 }}:
                            {{(cq.s2? "Intervall" :  cq.a2.length + " Antwort(en)" )}}

                            <v-icon class="ml-1" @click="copyQcQuestion(index)"> mdi-content-duplicate</v-icon>
                            <v-icon right @click="deleteDialog = true"> mdi-minus-circle-outline</v-icon>
                        </v-chip>

                        <div v-if="index === currentId" style="font-style: italic;">(geöffnet)</div>
                    </v-row>
                </div>
            </v-card>
        </v-card>
    </v-dialog>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
    name: 'ControlQuestions',
    props: {
        initialPollId: {
            type: Number,
            default: -1,
        },
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
        useEvaluationStore: {
            type: Boolean,
            default: true,
        },
        // pollsProp: {
        //     default: () => [],
        //     type: Array,
        // },
    },
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
            range: [0, 0],
            range2: [0, 0],
            answerTitlesDisplayedInSelect: 5,
            listOfControlQuestions: [],
            buttonInfo: '',
            currentId: -1,
            currentServerId: -1,
            questionWasCopied: false,
            deleteDialog: false,
            panel: false,
            dialog: false,
            pollsProp: [],
        }
    },

    created() {
        // this.categoryIndex = this.initialCategoryIndex
        // this.questionIndex = this.initialQuestionIndex
        // this.answerIndices = this.initialAnswerIndices
        console.log('create')

        this.initialize()
    },
    computed: {
        ...mapGetters({
            pollsFromEval: 'evaluation/getPolls',
        }),

        /**
         * gives a list of polls
         * @returns {string|*[]}
         */

        polls() {
            console.log('polls comp')
            console.log(this.pollsProp)
            if (this.useEvaluationStore) {
                return this.pollsFromEval
            } else if (this.pollsProp !== undefined && this.pollsProp.length !== 0) {
                console.log('returned the wrong')
                return [this.pollsProp]
            } else {
                return [{ pollName: '' }]
            }
        },

        /**
         * gives the name of the poll
         * @returns {any}
         */
        pollName() {
            console.log('pollName')
            console.log(this.polls, this.pollIndex)
            return this.polls[this.pollIndex].pollName
        },

        /**
         * gives the name of all the categories
         * @returns {[]}
         */

        categoryTitles() {
            const l = []
            for (let i = 0; i < this.polls[this.pollIndex].categoryList.length; i++) {
                l[i] = this.polls[this.pollIndex].categoryList[i].categoryName
            }
            return l
        },

        /**
         * gives a list of all the categories
         * @returns {[]|default.methods.poll.categoryList}
         */

        categories() {
            return this.polls[this.pollIndex].categoryList
        },

        /**
         *
         * gives a list of all the questionTitles in the first selected category
         * @returns {[]|*[]}
         */

        questionTitles() {
            if (this.categoryIndex === -1) {
                return []
            } else {
                const l = []
                for (let i = 0; i < this.questions.length; i++) {
                    l[i] = this.questions[i].questionMessage
                }
                return l
            }
        },

        /**
         *
         * gives a list of all the questionTitles in the second selected category
         * @returns {[]|*[]}
         */

        questionTitles2() {
            if (this.categoryIndex2 === -1) {
                return []
            } else {
                const l = []
                for (let i = 0; i < this.questions2.length; i++) {
                    l[i] = this.questions2[i].questionMessage
                }
                return l
            }
        },

        /**
         * gives a list of all the questions in the first selected category
         * @returns {[]|*[]}
         */

        questions() {
            if (this.categoryIndex === -1) {
                return []
            } else {
                const c = []
                for (let i = 0; i < this.categories[this.categoryIndex].questionList.length; i++) {
                    if (
                        this.categories[this.categoryIndex].questionList[i].questionType !== 'TextQuestion' &&
                        this.categories[this.categoryIndex].questionList[i].questionType !== 'SortQuestion'
                    ) {
                        c.push(this.categories[this.categoryIndex].questionList[i])
                    }
                }
                return c
            }
        },

        /**
         * gives a list of all the questions in the second selected category
         * @returns {[]|*[]}
         */

        questions2() {
            if (this.categoryIndex2 === -1) {
                return []
            } else {
                const c = []
                for (let i = 0; i < this.categories[this.categoryIndex2].questionList.length; i++) {
                    if (
                        this.categories[this.categoryIndex2].questionList[i].questionType !== 'TextQuestion' &&
                        this.categories[this.categoryIndex2].questionList[i].questionType !== 'SortQuestion'
                    ) {
                        c.push(this.categories[this.categoryIndex2].questionList[i])
                    }
                }
                return c
            }
        },

        /**
         * gives a list of all the answerTitles in the first selected category
         * @returns {[]|*[]}
         */

        answerTitles() {
            if (this.questionIndex === -1) {
                return []
            } else {
                const l = []
                console.log('answerTitles')
                console.log(this.questionIndex)
                console.log(this.questions)
                if (this.questions[this.questionIndex].questionType === 'RangeQuestion') {
                    l.push(this.questions[this.questionIndex].belowMessage)
                    for (
                        let i = this.questions[this.questionIndex].startValue;
                        i < this.questions[this.questionIndex].endValue;
                        i += this.questions[this.questionIndex].stepSize
                    ) {
                        l.push('' + i + ' - ' + (i + this.questions[this.questionIndex].stepSize))
                    }
                    l.push(this.questions[this.questionIndex].aboveMessage)
                } else {
                    for (let i = 0; i < this.questions[this.questionIndex].answerPossibilities.length; i++) {
                        l[i] = this.questions[this.questionIndex].answerPossibilities[i]
                    }
                }
                return l
            }
        },

        /**
         * gives a list of all the answerTitles in the second selected category
         * @returns {[]|*[]}
         */

        answerTitles2() {
            if (this.questionIndex2 === -1) {
                return []
            } else {
                const l = []
                if (this.questions2[this.questionIndex2].questionType === 'RangeQuestion') {
                    l.push(this.questions2[this.questionIndex2].belowMessage)
                    for (
                        let i = this.questions2[this.questionIndex2].startValue;
                        i < this.questions2[this.questionIndex2].endValue;
                        i += this.questions2[this.questionIndex2].stepSize
                    ) {
                        l.push('' + i + ' - ' + (i + this.questions2[this.questionIndex2].stepSize))
                    }
                    l.push(this.questions2[this.questionIndex2].aboveMessage)
                } else {
                    for (let i = 0; i < this.questions2[this.questionIndex2].answerPossibilities.length; i++) {
                        l[i] = this.questions2[this.questionIndex2].answerPossibilities[i]
                    }
                }
                return l
            }
        },

        // this needs to be computed, so we can get it from the nuxt Config

        /**
         * gives a string you can put into a style prop that sets the background
         * to the background color of the current theme
         * @returns {string}
         */

        frameColor() {
            return 'background-color:' + this.$vuetify.theme.currentTheme.background2 + ';'
        },
    },

    methods: {
        async initialize() {
            console.log('getOnePoll')

            await this.$axios
                .get('/getonepoll', {
                    params: {
                        pollId: this.initialPollId,
                    },
                })
                .then((response) => {
                    this.pollsProp = response.data
                    console.log('pollData')
                    console.log(this.pollsProp)
                })
                .catch((error) => {
                    console.log(error)
                })

            console.log('wert')

            // this.computeWordsFromIndices()

            await this.$axios
                .get('/poll/' + this.polls[this.pollIndex].pollId + '/consistencyquestions')
                .then((response) => {
                    const list = response.data
                    console.log(response.data)
                    this.listOfControlQuestions = []

                    for (let i = 0; i < list.length; i++) {
                        console.log(i)

                        const a1 = []
                        for (let j = 0; j < list[i].answer1Indices.length; j++) {
                            a1.push(parseFloat(list[i].answer1Indices[j]))
                        }

                        const a2 = []
                        for (let j = 0; j < list[i].answer2Indices.length; j++) {
                            a2.push(parseFloat(list[i].answer2Indices[j]))
                        }

                        this.listOfControlQuestions.push({
                            c1: this.getCategoryIndexAndQuestionIndexFromQuestionId(list[i].question1Id).categoryIndex,
                            q1: this.getCategoryIndexAndQuestionIndexFromQuestionId(list[i].question1Id).questionIndex,
                            a1,
                            s1: list[i].question1Slider,
                            c2: this.getCategoryIndexAndQuestionIndexFromQuestionId(list[i].question2Id).categoryIndex,
                            q2: this.getCategoryIndexAndQuestionIndexFromQuestionId(list[i].question2Id).questionIndex,
                            a2,
                            s2: list[i].question2Slider,
                            serverId: list[i].consistencyQuestionId,
                        })
                    }
                    console.log(this.listOfControlQuestions)
                })
                .catch((reason) => {
                    console.log(reason)
                })
        },
        // All these methods update the indices of values according to the strings that were set for them
        updateCategoryIndex() {
            this.categoryIndex = this.categoryTitles.indexOf(this.selectedCategory)
        },

        updateCategoryIndex2() {
            this.categoryIndex2 = this.categoryTitles.indexOf(this.selectedCategory2)
        },

        updateQuestionIndex() {
            this.questionIndex = this.questionTitles.indexOf(this.selectedQuestion)
            this.resetAnswers()
        },

        updateQuestionIndex2() {
            this.questionIndex2 = this.questionTitles2.indexOf(this.selectedQuestion2)
            this.resetAnswers2()
        },

        updateAnswerIndices() {
            console.log('update Answers ')

            this.answerIndices = []
            for (let i = 0; i < this.selectedAnswers.length; i++) {
                this.answerIndices[i] = this.answerTitles.indexOf(this.selectedAnswers[i])
            }
        },

        updateAnswerIndices2() {
            console.log('update Answers 2 ')
            this.answerIndices2 = []
            for (let i = 0; i < this.selectedAnswers2.length; i++) {
                this.answerIndices2[i] = this.answerTitles2.indexOf(this.selectedAnswers2[i])
            }
        },

        /**
         * resets answers for the first question
         * has to do range as well as answerIndices in case it was a choice or slider question
         *
         * **/

        resetAnswers() {
            this.range = [0, 0]
            this.answerIndices = []
        },

        /**
         * resets answers for the second question
         * has to do range as well as answerIndices in case it was a choice or slider question
         *
         * **/

        resetAnswers2() {
            this.range2 = [0, 0]
            this.answerIndices2 = []
        },

        /**
         * This method queries if there is more than one category or not
         * if there is only one category it automatically sets the chosen category to the first and only one available
         *
         * @returns {boolean} true if there was only one category
         */

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

        /**
         * adds the information that is currently edited to the list of CQs
         * send that informatio to the store
         *
         * **/
        addControlQuestion() {
            this.updateAnswerIndices()
            this.updateAnswerIndices2()
            console.log('add')
            if (
                this.categoryIndex === -1 ||
                this.questionIndex === -1 ||
                (this.answerIndices.length === 0 &&
                    !(this.questions[this.questionIndex].questionType === 'SliderQuestion')) ||
                this.categoryIndex2 === -1 ||
                this.questionIndex2 === -1 ||
                (this.answerIndices2.length === 0 &&
                    !(this.questions2[this.questionIndex2].questionType === 'SliderQuestion'))
            ) {
                this.buttonInfo = 'Bitte füllen Sie erst alle Felder aus!'
            } else {
                const ans1 = []
                const ans2 = []
                let slide = false
                let slide2 = false

                if (this.questions[this.questionIndex].questionType === 'SliderQuestion') {
                    if (this.range[0] === this.range[1]) {
                        this.buttonInfo = 'Bitte geben Sie bei der ersten Frage ein echtes Intervall an'
                        console.log('no add')
                        return
                    } else {
                        slide = true
                        ans1[0] = this.range[0] + ''
                        ans1[1] = this.range[1] + ''
                    }
                } else {
                    for (let i = 0; i < this.answerIndices.length; i++) {
                        ans1[i] = this.answerIndices[i] + ''
                    }
                }

                if (this.questions2[this.questionIndex2].questionType === 'SliderQuestion') {
                    if (this.range2[0] === this.range2[1]) {
                        this.buttonInfo = 'Bitte geben Sie bei der zweiten Frage ein echtes Intervall an'
                        return
                    } else {
                        slide2 = true
                        ans2[0] = this.range2[0] + ''
                        ans2[1] = this.range2[1] + ''
                    }
                } else {
                    for (let i = 0; i < this.answerIndices2.length; i++) {
                        ans2[i] = this.answerIndices2[i] + ''
                    }
                }

                const cq = {
                    c1: this.categoryIndex,
                    q1: this.questionIndex,
                    a1: ans1,
                    s1: slide,
                    c2: this.categoryIndex2,
                    q2: this.questionIndex2,
                    a2: ans2,
                    s2: slide2,
                    serverId: this.currentServerId,
                }
                console.log(cq)

                const consistencyQuestion = {
                    consistencyQuestionId: null,
                    pollId: this.polls[this.pollIndex].pollId,
                    question1Id: this.questions[this.questionIndex].questionId,
                    question1Slider: slide,
                    answer1Indices: ans1,
                    question2Id: this.questions2[this.questionIndex2].questionId,
                    question2Slider: slide2,
                    answer2Indices: ans2,
                }

                if (this.currentServerId !== -1) {
                    consistencyQuestion.consistencyQuestionId = this.currentServerId

                    this.$axios.post('/poll/editcq/' + this.currentServerId, consistencyQuestion).catch((reason) => {
                        console.log(reason + ' in edit')
                    })
                } else {
                    this.$axios
                        .post('/poll/' + this.polls[this.pollIndex].pollId + '/addcq', consistencyQuestion)
                        .then((response) => {
                            cq.serverId = response.data.consistencyQuestionId
                            console.log('id')
                            console.log(response.data.consistencyQuestionId)
                            console.log(cq.serverId)
                        })
                        .catch((reason) => {
                            console.log(reason + ' auch in edit')
                        })
                }

                if (this.currentId === -1) {
                    this.listOfControlQuestions.push(cq)
                } else {
                    this.listOfControlQuestions[this.currentId] = cq
                }
                console.log('bin hier')
                console.log(cq)

                this.buttonInfo = ''
                this.currentId = -1
                this.currentServerId = -1
                this.categoryIndex = -1
                this.questionIndex = -1
                this.answerIndices = []
                this.categoryIndex2 = -1
                this.questionIndex2 = -1
                this.answerIndices2 = []
                this.computeWordsFromIndices()
            }
        },

        /**
         *  deletes the CQ that is currently edited from the editors window, but still keeps its former version.
         * **/

        discardControlQuestion() {
            this.buttonInfo = ''
            this.currentId = -1
            this.currentServerId = -1
            this.categoryIndex = -1
            this.questionIndex = -1
            this.answerIndices = []
            this.range = [0, 0]
            this.categoryIndex2 = -1
            this.questionIndex2 = -1
            this.answerIndices2 = []
            this.range2 = [0, 0]
            this.computeWordsFromIndices()
        },

        /**
         * deletes a controlQuestion with a specific index
         * **/

        deleteControlQuestion(index) {
            // the question is added here, so its no longer visible in the editing component

            if (index === this.currentId) {
                this.addControlQuestion(index)
            }

            console.log('delete')
            if (index > -1) {
                this.$axios.post('/poll/delcq/' + this.listOfControlQuestions[index].serverId).catch((reason) => {
                    console.log(reason + ' in delete')
                })
                this.listOfControlQuestions.splice(index, 1)
                console.log(this.listOfControlQuestions)
                console.log(index)
            }
        },

        /**
         *
         * load the data of an CQ Question with a specific index into the editing window
         * **/

        openCq(index) {
            console.log('open')
            console.log(this.listOfControlQuestions[index])
            if (this.questionWasCopied) {
                this.currentId = -1
                this.currentServerId = -1
                this.questionWasCopied = false
            } else {
                this.currentId = index
                this.currentServerId = this.listOfControlQuestions[index].serverId
            }

            this.categoryIndex = this.listOfControlQuestions[index].c1
            this.questionIndex = this.listOfControlQuestions[index].q1

            if (this.listOfControlQuestions[index].s1) {
                this.range[0] = parseFloat(this.listOfControlQuestions[index].a1[0])
                this.range[1] = parseFloat(this.listOfControlQuestions[index].a1[1])
                this.$forceUpdate()
            } else {
                this.answerIndices = []

                for (let i = 0; i < this.listOfControlQuestions[index].a1.length; i++) {
                    this.answerIndices[i] = this.listOfControlQuestions[index].a1[i]
                }
            }

            this.categoryIndex2 = this.listOfControlQuestions[index].c2
            this.questionIndex2 = this.listOfControlQuestions[index].q2

            if (this.listOfControlQuestions[index].s2) {
                this.range2[0] = parseFloat(this.listOfControlQuestions[index].a2[0])
                this.range2[1] = parseFloat(this.listOfControlQuestions[index].a2[1])
                this.$forceUpdate()
            } else {
                this.answerIndices2 = []

                for (let i = 0; i < this.listOfControlQuestions[index].a2.length; i++) {
                    this.answerIndices2[i] = this.listOfControlQuestions[index].a2[i]
                }
            }

            console.log('open')

            console.log('a1: ')
            console.log(this.listOfControlQuestions[index].a1)

            console.log('answerIndices: ')
            console.log(this.answerIndices)

            this.computeWordsFromIndices()
        },

        /**
         * sets the selected attributes using the information from the indices
         *
         * **/

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

        /**
         * sets variable that indicates that a question was just copied
         *
         *
         **/

        copyQcQuestion() {
            this.questionWasCopied = true
        },

        /**
         * sets deleteDialog false
         *
         *
         **/

        closeDeleteDialog() {
            this.deleteDialog = false
        },

        /**
         * computes the index of the category a question is in, as well as the index that question has within that category
         * @param questionId of the question
         * @returns {{questionIndex: number, categoryIndex: number}|null}
         */

        getCategoryIndexAndQuestionIndexFromQuestionId(questionId) {
            console.log('bin in getCat...')
            for (let c = 0; c < this.categories.length; c++) {
                console.log(c)
                const questions = []
                for (let i = 0; i < this.categories[c].questionList.length; i++) {
                    if (
                        this.categories[c].questionList[i].questionType !== 'SortQuestion' &&
                        this.categories[c].questionList[i].questionType !== 'TextQuestion'
                    )
                        questions.push(this.categories[c].questionList[i])
                }
                for (let q = 0; q < questions.length; q++) {
                    console.log(q)
                    if (questions[q].questionId === questionId) {
                        console.log({ categoryIndex: c, questionIndex: q })
                        return { categoryIndex: c, questionIndex: q }
                    }
                }
            }
            return null
        },
    },
}
</script>

<style scoped></style>
