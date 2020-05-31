<template>
    <v-row no-gutters>
        <v-col>
            <v-list-item-title>
                {{ choppedTitle }}
            </v-list-item-title>
            <v-list-item-subtitle>
                {{ translatedQuestionType }}
            </v-list-item-subtitle>
        </v-col>
        <v-spacer />
        <v-spacer />
        <v-spacer />
        <v-spacer />
        <v-col>
            <v-btn icon @click="set()">
                <v-icon small>
                    mdi-pencil
                </v-icon>
            </v-btn>
        </v-col>
    </v-row>
</template>

<script>
import { mapMutations } from 'vuex'
export default {
    name: 'QuestionListElement',
    props: {
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
        choppedTitle() {
            if (this.questionMessage.length > 35) {
                return this.questionMessage.substr(0, 35) + '...?'
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
    },
    methods: {
        ...mapMutations({ setToLoad: 'pollOverview/setToLoad' }),
        set() {
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
