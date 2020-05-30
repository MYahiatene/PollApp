<template>
    <v-card
        :color="kategories[4].color"
        close
        v-if="filter.type === 'questionAnswer'"
        @click:close="deleteFromFilterList(filter)"
    >
        Nur Teilnehmer, die bei Frage {{ selectedQuestion }}
        <v-overflow-btn :items="Questions" v-model="selectedQuestion" elevation="0" style="box-shadow: none;">
        </v-overflow-btn>
        die Antwort {{ selectedAnswer }}
        <v-overflow-btn :items="Answers" v-model="selectedAnswer" flat elevation="0" style="box-shadow: none;">
        </v-overflow-btn>
        ausgewählt haben.
    </v-card>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
export default {
    name: 'QAFilter',
    props: {
        pollIndex:{
            type: Number,
        }
    },
    computed: {
        ...mapGetters({
            polls: 'evaluation/getPolls',
        }),
    },
}
</script>

<style scoped></style>
