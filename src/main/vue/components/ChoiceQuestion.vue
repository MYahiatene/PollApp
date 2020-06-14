<!--this widget enables the user to configure a choice question including all of its unique features-->
<template>
    <v-container>
        <v-row no-gutters>
            <v-overflow-btn v-model="questionChoiceType" :items="choiceType" label="Antwortart"></v-overflow-btn>
        </v-row>
        <v-form>
            <v-row v-for="(answer, index) in answerPossibilities" :key="index" no-gutters>
                <v-text-field
                    v-model="answerPossibility"
                    :value="answer"
                    :label="'Antwortmöglichkeit ' + (index + 1)"
                    @input="
                        (answer) => {
                            obj.answer = answer
                            obj.index = index
                        }
                    "
                ></v-text-field>
            </v-row>
            <v-row>
                <v-col cols="1">
                    <v-btn @click="addAnswer">
                        <v-icon color="primary" left>
                            mdi-plus
                        </v-icon>
                        Antwort
                    </v-btn>
                </v-col>
            </v-row>
            <v-row></v-row>
            <v-row no-gutters>
                <v-text-field
                    label="Maximale Antwortanzahl"
                    :hint="
                        'Maximale Anzahl gleichzeitig auswählbarer Antworten (1 bis ' +
                        (getQuestion.answerPossibilities.length - 1) +
                        ')'
                    "
                    type="number"
                    v-model="nrPossibleAnswers"
                    min="1"
                    :max="getQuestion.answerPossibilities.length - 1"
                    step="1"
                    value="1"
                    :rules="answerCountRules"
                ></v-text-field> </v-row
        ></v-form>
        <v-row no-gutters>
            <v-switch label="Teilnehmer darf eigene Antworten hinzufügen" v-model="userAnswersPossible"></v-switch>
        </v-row>
    </v-container>
</template>

<script>
import { mapGetters, mapMutations, mapActions } from 'vuex'

export default {
    name: 'ChoiceQuestion',
    props: { pollData: { type: Object }, questionData: { type: Object }, buildIndex: { type: Number } },
    data() {
        return {
            choiceType: ['Standardauswahl', 'Drop-Down', 'Sortieren'],
            obj: {},
            answerCountRules: [
                (v) =>
                    parseFloat(v) < this.getQuestion.answerPossibilities.length ||
                    'Es könnten keine ' +
                        v +
                        ' Antworten ausgewählt werden, weil bisher nur ' +
                        (this.getQuestion.answerPossibilities.length - 1) +
                        ' Antwortmöglichkeit' +
                        (this.getQuestion.answerPossibilities.length - 1 > 1 ? 'en' : '') +
                        ' erstellt wurde' +
                        (this.getQuestion.answerPossibilities.length - 1 > 1 ? 'n' : '') +
                        '!',
            ],
        }
    },
    computed: {
        ...mapGetters({ getQuestion: 'questionOverview/getQuestion', questionToLoad: 'pollOverview/questionToLoad' }),
        nrPossibleAnswers: {
            get() {
                return this.getQuestion.numberOfPossibleAnswers
            },
            set(number) {
                this.setNrOfPossibleAnswers(parseInt(number))
            },
        },
        questionChoiceType: {
            get() {
                return this.getQuestion.choiceType
            },
            set(value) {
                this.setChoiceType(value)
            },
        },
        userAnswersPossible: {
            get() {
                return this.getQuestion.userAnswers
            },
            set(value) {
                this.setUserAnswers(value)
            },
        },
        answerPossibilities: {
            get() {
                return this.getQuestion.answerPossibilities
            },
        },
        answerPossibility: {
            get() {
                return this.getQuestion.answerPossibilities
            },
            set() {
                console.log(this.obj)
                this.setAnswerPossibility(this.obj)
            },
        },
    },
    methods: {
        ...mapMutations({
            setNrOfPossibleAnswers: 'pollOverview/setNrOfPossibleAnswers',
            updateAnswer: 'pollOverview/updateAnswer',
            removeAnswer: 'pollOverview/removeAnswer',
            setChoiceType: 'questionOverview/setChoiceType',
            setUserAnswers: 'questionOverview/setUserAnswers',
            addAnswer: 'questionOverview/addAnswer',
        }),
        ...mapActions({
            setAnswerPossibility: 'questionOverview/setAnswerPossibility',
        }),
        updateSingleAnswer(index, text) {
            const payload = {
                i: index,
                t: text,
            }
            this.updateAnswer(payload)
        },
    },
}
</script>
