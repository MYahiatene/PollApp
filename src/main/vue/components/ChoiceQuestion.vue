<!--this widget enables the user to configure a choice question including all of its unique features-->
<template>
    <v-container>
        <!--<v-row no-gutters>
            <v-overflow-btn v-model="questionChoiceType" :items="choiceType" label="Antwortart"></v-overflow-btn>
        </v-row> -->
        <v-form>
            <v-row v-for="(answer, index) in answerPossibilities" :key="index" v-model="answerPossibilities" no-gutters>
                <v-text-field
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
                <v-col cols="10"></v-col>
                <v-col cols="2">
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
                    :hint="'Maximale Anzahl gleichzeitig auswählbarer Antworten'"
                    type="number"
                    v-model="numberOfPossibleAnswers"
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
                    (parseFloat(v) >= 1 && parseFloat(v) <= this.getQuestion.answerPossibilities.length) ||
                    'Die maximale Anwortzahl kann sich nur zwischen 1 und ' +
                        (this.getQuestion.answerPossibilities.length - 1) +
                        ' Antworten befinden',
            ],
        }
    },
    computed: {
        ...mapGetters({ getQuestion: 'questionOverview/getQuestion', questionToLoad: 'pollOverview/questionToLoad' }),
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
            set() {
                console.log(this.obj)
                this.setAnswerPossibility(this.obj)
            },
        },
        numberOfPossibleAnswers: {
            get() {
                return this.getQuestion.numberOfPossibleAnswers
            },
            set(value) {
                this.setNumberOfPossibleAnswers(value)
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
            setNumberOfPossibleAnswers: 'questionOverview/setNumberOfPossibleAnswers',
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
