<template>
    <!--    Here we need the negative margin in order to cancel out the margin applied by the v-cards-->
    <v-card outlined hover flat :color="color" class="my-n1">
        <v-row no-gutters class="ma-n2 my-n4 pa-4">
            <v-col cols="12" lg="10" md="10" sm="10">
                <v-list-item-title>
                    {{ choppedTitle }}
                </v-list-item-title>
                <v-list-item-subtitle>
                    {{ translatedQuestionType }}
                </v-list-item-subtitle>
            </v-col>
            <v-col cols="12" lg="2" md="2" sm="2">
                <v-btn icon @click="setIDs()" color="primary">
                    <v-icon small>
                        mdi-pencil
                    </v-icon>
                </v-btn>
            </v-col>
        </v-row>
    </v-card>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'

export default {
    name: 'QuestionListElement',

    props: {
        pollData: { type: Object },
        buildIndex: { type: Number },
        pollId: {
            type: Number,
        },
        categoryId: {
            type: Number,
        },
        questionId: {
            type: Number,
        },
        questionMessage: {
            type: String,
        },
        questionType: {
            type: String,
        },
    },
    computed: {
        ...mapGetters({
            currentQuestion: 'pollOverview/getQuestion',
        }),
        choppedTitle() {
            if (this.questionMessage.length > 30) {
                return this.questionMessage.substr(0, 25) + '...?'
            } else {
                return this.questionMessage
            }
        },
        translatedQuestionType() {
            switch (this.questionType) {
                case 'ChoiceQuestion':
                    return 'Auswahlfrage'
                case 'TextQuestion':
                    return 'Freitextfrage'
                case 'RangeQuestion':
                    return 'Intervallfrage'
                default:
                    return ''
            }
        },

        // the color is applied to the question when its the one being edited, so the user can see it more easily

        color() {
            if (this.currentQuestion !== null) {
                if (
                    this.currentQuestion.questionMessage === this.questionMessage &&
                    this.currentQuestion.categoryId === this.categoryId &&
                    this.currentQuestion.questionId === this.questionId
                ) {
                    return this.$vuetify.theme.currentTheme.softAccent
                } else {
                    return ''
                }
            }

            return ''
        },
    },
    methods: {
        ...mapMutations({ setToLoad: 'pollOverview/setToLoad' }),
        setIDs() {
            this.buildIndex = 2
            const IDs = {
                pollID: this.pollId,
                categoryID: this.categoryId,
                questionID: this.questionId,
            }
            this.setToLoad(IDs)
        },
    },
}
</script>

<style scoped></style>
