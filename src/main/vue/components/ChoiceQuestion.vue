<!--this widget enables the user to configure a choice question including all of its unique features-->
<template>
    <v-container>
        <v-row no-gutters>
            <v-overflow-btn :items="choiceType" label="Antwortart"></v-overflow-btn>
        </v-row>
        <v-row v-for="(answer, index) in getQuestion.answerPossibilities" :key="index" no-gutters>
            <v-text-field
                :value="getQuestion.answerPossibilities[index]"
                :label="'Antwortmöglichkeit ' + (index + 1)"
                @input="(text) => updateAnswer({ index, text })"
            ></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-text-field
                label="Antwortmöglichkeiten"
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
            ></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-switch label="Teilnehmer darf eigene Antworten hinzufügen" v-model="userAnswersPossible"></v-switch>
        </v-row>
    </v-container>
</template>

<script>
import { mapMutations, mapGetters } from 'vuex'
export default {
    name: 'ChoiceQuestion',
    data() {
        return {
            choiceType: ['Standartauswahl', 'Drop-Down', 'Sortieren'],
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
                console.log('setter')
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
