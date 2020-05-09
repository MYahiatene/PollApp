<template>
    <v-container class="mt-5">
        <v-app-bar fixed app elevate-on-scroll>
            <v-toolbar-title> {{ PollResult.name }}</v-toolbar-title>

            <v-spacer></v-spacer>

            <v-btn>
                Analyse
            </v-btn>

            <v-btn icon>
                <v-icon>mdi-dots-vertical</v-icon>
            </v-btn>
        </v-app-bar>

        <v-card v-for="question in PollResult.questionList" :key="question.id">
            <ChoiceQuestionEvaluationWidget
                :question-title="question.title"
                :question-id="question.id"
            ></ChoiceQuestionEvaluationWidget>
        </v-card>
    </v-container>
</template>

<script>
import ChoiceQuestionEvaluationWidget from '../components/ChoiceQuestionEvaluationWidget'

export default {
    name: 'BaseEvaluationPage',
    components: { ChoiceQuestionEvaluationWidget },
    data() {
        return {
            PollResult: {
                name: 'Mittagessen Umfrage',
                questionList: [
                    { id: 1, title: 'How are you?' },
                    { id: 2, title: 'What is your gender?' },
                    { id: 3, title: 'Do you like cake?' },
                ],
            },
        }
    },
}
</script>

<style scoped></style>
