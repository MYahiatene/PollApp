<template>
    <v-container>
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
                ></v-text-field> </v-row
            ><v-row>
                <v-col cols="8"></v-col>
                <v-col cols="2">
                    <v-btn @click="addAnswer">
                        <v-icon color="primary" left>
                            mdi-plus
                        </v-icon>
                        Antwort
                    </v-btn>
                </v-col>
            </v-row>
        </v-form>
    </v-container>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
export default {
    name: 'SortQuestion',
    data() {
        return {
            obj: {},
        }
    },
    computed: {
        ...mapGetters({ getQuestion: 'questionOverview/getQuestion' }),
        questionChoiceType: {
            get() {
                return this.getQuestion.choiceType
            },
            set(value) {
                this.setChoiceType(value)
            },
        },
        answerPossibilities: {
            get() {
                return this.getQuestion.answerPossibilities
            },
            set() {
                this.setAnswerPossibility(this.obj)
            },
        },
    },
    methods: {
        ...mapMutations({
            setChoiceType: 'questionOverview/setChoiceType',
            setUserAnswers: 'questionOverview/setUserAnswers',
            addAnswer: 'questionOverview/addAnswer',
            setNumberOfPossibleAnswers: 'questionOverview/setNumberOfPossibleAnswers',
        }),
        ...mapActions({
            setAnswerPossibility: 'questionOverview/setAnswerPossibility',
        }),
    },
}
</script>

<style scoped></style>
