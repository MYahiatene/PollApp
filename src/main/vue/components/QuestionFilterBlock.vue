<template>
    <v-card>
        <!--Umfrage?-->
        <!--Frage-->
        <v-overflow-btn
            v-model="questionSet"
            class="my-2"
            :items="questions2"
            label="Frage"
            @change="changeQuestionID()"
        ></v-overflow-btn>
        <!--Antwort-->
        <v-overflow-btn
            :v-show="(questionSet === '')"
            class="my-2"
            :items="answerList"
            label="Antwort"
        ></v-overflow-btn>

        <v-list-item-title>{{ item.title }}</v-list-item-title>
    </v-card>
</template>

<script>
export default {
    name: 'QuestionFilterBlock',
    props: {
        questions: {
            type: [Object],
        },
    },
}
</script>

<style scoped></style>
