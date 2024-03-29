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
            <v-row></v-row>
            <v-row no-gutters>
                <v-col>
                    <v-switch
                        v-model="multipleChoice"
                        @change="updateNumOfPosAns()"
                        label="Mehrere Antworten erlauben."
                    >
                    </v-switch>
                </v-col>
                <v-col>
                    <v-text-field
                        v-if="multipleChoice"
                        v-model="numberOfPossibleAnswers"
                        label="Maximale Antwortanzahl"
                        :hint="'Maximale Anzahl gleichzeitig auswählbarer Antworten'"
                        type="number"
                        min="1"
                        :max="getQuestion.answerPossibilities.length"
                        step="1"
                        :value="getQuestion.answerPossibilities.length"
                        :rules="answerCountRules"
                        required
                    ></v-text-field>
                </v-col> </v-row
        ></v-form>
        <v-row no-gutters>
            <v-switch v-model="dropDownPossible" label="Als DropDown Frage zeigen"></v-switch>
        </v-row>
        <v-row no-gutters>
            <v-switch v-model="userAnswersPossible" label="Teilnehmer darf eigene Antworten hinzufügen"></v-switch>
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
                (v) => {
                    if (parseFloat(v) >= 1 && parseFloat(v) <= this.getQuestion.answerPossibilities.length) {
                        this.setSaveButtonStatus(true)
                        return ''
                    } else {
                        this.setSaveButtonStatus(false)
                        return (
                            'Die maximale Anwortzahl kann sich nur zwischen 1 und ' +
                            this.getQuestion.answerPossibilities.length +
                            ' Antworten befinden'
                        )
                    }
                },
            ],
            multipleChoice: false,
        }
    },
    mounted() {
        if (this.numberOfPossibleAnswers > 1) {
            this.multipleChoice = true
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
        dropDownPossible: {
            get() {
                return this.getQuestion.dropDown
            },
            set(value) {
                this.setDropDown(value)
            },
        },
        answerPossibilities: {
            get() {
                return [...this.getQuestion.answerPossibilities, '']
            },
            set() {
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
            setSaveButtonStatus: 'questionOverview/setSaveButtonStatus',
            setNrOfPossibleAnswers: 'pollOverview/setNrOfPossibleAnswers',
            updateAnswer: 'pollOverview/updateAnswer',
            removeAnswer: 'pollOverview/removeAnswer',
            setChoiceType: 'questionOverview/setChoiceType',
            setUserAnswers: 'questionOverview/setUserAnswers',
            setDropDown: 'questionOverview/setDropDown',
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

        updateNumOfPosAns() {
            if (!this.multipleChoice) {
                this.numberOfPossibleAnswers = 1
            } else {
                this.numberOfPossibleAnswers = this.getQuestion.answerPossibilities.length
            }
        },
    },
}
</script>
