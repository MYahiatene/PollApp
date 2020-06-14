<!--this widget enables the user to configure a choice question including all of its unique features-->
<template>
    <v-container>
        <v-row no-gutters>
            <v-overflow-btn :items="choiceType" label="Antwortart"></v-overflow-btn>
        </v-row>
        <v-form>
            <v-row v-for="(answer, index) in getQuestion.answerPossibilities" :key="index" no-gutters>
                <v-text-field
                    :value="getQuestion.answerPossibilities[index]"
                    :label="'Antwortmöglichkeit ' + (index + 1)"
                    @input="(text) => updateAnswer({ index, text })"
                ></v-text-field>
            </v-row>
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
import { mapGetters, mapMutations } from 'vuex'

export default {
    name: 'ChoiceQuestion',
    props: { pollData: { type: Array }, questionData: { type: Object }, buildIndex: { type: Number } },
    data() {
        return {
            choiceType: ['Standartauswahl', 'Drop-Down', 'Sortieren'],
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
        ...mapGetters({ getQuestion: 'pollOverview/getQuestion', questionToLoad: 'pollOverview/questionToLoad' }),
        nrPossibleAnswers: {
            get() {
                return this.getQuestion.numberOfPossibleAnswers
            },
            set(number) {
                this.setNrOfPossibleAnswers(parseInt(number))
            },
        },
        userAnswersPossible: {
            get() {
                return this.getQuestion.userAnswers
            },
            set(active) {
                this.setUserAnswers(active)
            },
        },
    },
    methods: {
        ...mapMutations({
            setNrOfPossibleAnswers: 'pollOverview/setNrOfPossibleAnswers',
            setUserAnswers: 'pollOverview/setUserAnswers',
            updateAnswer: 'pollOverview/updateAnswer',
            addAnswer: 'pollOverview/addAnswer',
            removeAnswer: 'pollOverview/removeAnswer',
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
