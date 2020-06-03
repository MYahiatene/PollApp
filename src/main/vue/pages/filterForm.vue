<template>
    <v-card class="ma-2 pa-2">
        <v-card-title>Erweiterte Analyse {{ polls.length }}</v-card-title>
        <template>
            <v-overflow-btn prefix="Basisdaten:" :items="pollTitles" dense v-model="chosenPoll"> </v-overflow-btn>
            <v-expansion-panels accordion multiple hover>
                <v-expansion-panel>
                    <v-expansion-panel-header>
                        Fragenauswahl
                    </v-expansion-panel-header>
                    <v-expansion-panel-content>
                        Hier können Sie die Fragen auswählen, die in der Analyse betrachtet werden sollen.
                    </v-expansion-panel-content>
                </v-expansion-panel>

                <v-expansion-panel>
                    <v-expansion-panel-header>
                        Teilnehmerfilter
                    </v-expansion-panel-header>
                    <v-expansion-panel-content>
                        Hier können Sie die Teilnehmer, deren Ergebnisse betrachtet werden filtern.
                    </v-expansion-panel-content>
                </v-expansion-panel>

                <v-expansion-panel>
                    <v-expansion-panel-header>
                        Antwortenfilter
                    </v-expansion-panel-header>
                    <v-expansion-panel-content dense>
                        Hier können Sie das Antwortverhalten der Teilnehmer, die Sie betrachten wollen festlegen.
                        <v-divider></v-divider>
                        <v-row>
                            <v-col colls="12" lg="10">
                                <v-checkbox
                                    v-model="applyConsistency"
                                    label="Betrachte nur Teilnehmer, die alle Konsistenzfragen konsistent beantwortet haben."
                                >
                                </v-checkbox>
                            </v-col>
                            <v-col align="left">
                                <v-btn>
                                    Bearbeiten
                                </v-btn>
                            </v-col>
                        </v-row>

                        <v-divider></v-divider>

                        <v-row>
                            <v-col colls="12" lg="10">
                                <v-checkbox v-model="qafilter" label="Weitere Filter..."> </v-checkbox>
                            </v-col>
                            <v-col v-if="qafilter" align="left">
                                <v-btn @click="addQAFilter"> + </v-btn>
                            </v-col>
                        </v-row>

                        <div v-if="qafilter">
                            <div v-for="(filter, index) in filterList" :key="index">
                                <v-card class="pa-2 ma-2">
                                    <v-row>
                                        <v-col align="right" colls="12" lg="10">
                                            <v-overflow-btn
                                                prefix="Nur Teilnehmer, die in Kategorie"
                                                dense
                                                :items="categories"
                                                v-model="selectedCategory"
                                                elevation="0"
                                                style="box-shadow: none;"
                                            >
                                            </v-overflow-btn>
                                        </v-col>

                                        <v-col>
                                            <v-btn icon @click="deleteQAFilter(index)">
                                                <v-icon> mdi-delete </v-icon>
                                            </v-btn>
                                            <v-btn icon @click="addQAFilter(index)">
                                                <v-icon> + </v-icon>
                                            </v-btn>
                                        </v-col>
                                    </v-row>

                                    <v-overflow-btn
                                        prefix="bei Frage"
                                        dense
                                        :items="questions"
                                        v-model="selectedQuestion"
                                        :no-data-text="'Keine Kategorie ausgewählt'"
                                        elevation="0"
                                        style="box-shadow: none;"
                                    >
                                    </v-overflow-btn>
                                    <v-overflow-btn
                                        prefix="die Antwort"
                                        :items="answers"
                                        dense
                                        v-model="selectedAnswer"
                                        flat
                                        :no-data-text="'Keine Frage ausgewählt'"
                                        elevation="0"
                                        style="box-shadow: none;"
                                        @input="updateData"
                                    >
                                    </v-overflow-btn>
                                    ausgewählt haben.
                                </v-card>
                            </div>
                        </div>
                    </v-expansion-panel-content>
                </v-expansion-panel>
            </v-expansion-panels>
        </template>
        <v-card-actions>
            <v-col align="right">
                <v-btn color="primary"> Anwenden </v-btn>
            </v-col>
        </v-card-actions>
    </v-card>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

export default {
    name: 'filterForm',
    data() {
        return {
            applyConsistency: false,
            qafilter: false,
            qafilterList: [{ filterType: 'qaFilter', categoryId: '', questionId: '', answerIds: [] }],
            chosenPoll: 'Umfrage IT-Messe 2020',
        }
    },

    methods: {
        ...mapActions({ initialize: 'evaluation/initialize' }),
        addQAFilter() {
            this.qafilterList.push({ filterType: 'qaFilter', categoryId: '', questionId: '', answerIds: [] })
        },

        deleteQAFilter(index) {
            this.qafilterList.splice(index, 1)
        },
    },

    mounted() {
        this.initialize()
    },

    computed: {
        ...mapGetters({
            polls: 'evaluation/getPolls',
        }),

        categoryIndex() {
            console.log(this.selectedCategory)
            return this.categories.indexOf(this.selectedCategory)
        },
        questionIndex() {
            return this.questions.indexOf(this.selectedQuestion)
        },
        pollTitles() {
            const pollTitles = Object.assign([{}], this.polls)
            for (let i = 0; i < pollTitles.length; i++) {
                pollTitles[i] = this.polls[i].pollName
            }
            return pollTitles
        },
        categories() {
            console.log('categories')
            const l = []
            for (let i = 0; i < this.polls[this.pollIndex].categoryList.length; i++) {
                l[i] = this.polls[this.pollIndex].categoryList[i].categoryName
            }
            return l
        },
        questions() {
            console.log('questions')
            if (this.selectedCategory === '') {
                return []
            } else {
                const categories = this.polls[this.pollIndex].categoryList
                const l = []
                for (let i = 0; i < categories[this.categoryIndex].questionList.length; i++) {
                    l[i] = categories[this.categoryIndex].questionList[i].questionMessage
                }
                return l
            }
        },
        answers() {
            console.log('Answers')
            if (this.selectedQuestion === '') {
                return []
            } else {
                const questions = this.polls[this.pollIndex].categoryList[this.categoryIndex].questionList
                const l = []
                for (let i = 0; i < questions[this.questionIndex].answerPossibilities.length; i++) {
                    l[i] = questions[this.questionIndex].answerPossibilities[i]
                }
                return l
            }
        },
    },
}
</script>

<style scoped></style>
