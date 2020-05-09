<template>
    <div>
        <h1>Frage {{ questionId }} : {{ questionTitle }}</h1>
    </div>
</template>

<script>
export default {
    props: {
        questionTitle: {
            type: String,
        },
        questionId: {
            type: Number,
        },
    },
    name: 'ChoiceQuestionEvaluationWidget',
}
</script>

<style scoped></style>
