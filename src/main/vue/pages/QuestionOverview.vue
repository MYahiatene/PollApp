<template>
    <v-container>
        <v-subheader class="display-2">Umfrage 1</v-subheader>
        <v-content>
            <v-row>
                <v-col cols="4">
                    <v-container fluid>
                        <v-flex class="ma-n4">
                            <v-list v-for="categorie in categories" :key="categorie.id" two-line>
                                <v-subheader class="headline">{{ categorie.name }}</v-subheader>
                                <draggable v-model="categories.items" category="questions">
                                    <v-list v-for="item in items" :key="item">
                                        <v-list-item-title :key="item.title">
                                            <v-list-item-content>
                                                <v-list-item-title>{{ item }}</v-list-item-title>
                                            </v-list-item-content>
                                            <v-list-item-action>
                                                <v-btn depressed small icon>
                                                    <v-icon>{{ item.symbol }}</v-icon>
                                                </v-btn>
                                            </v-list-item-action>
                                        </v-list-item-title>
                                    </v-list>
                                </draggable>
                            </v-list>
                        </v-flex>
                    </v-container>
                    <div class="ma-8" @click="createCategory()">
                        <v-btn depressed small>Neue Kategorie</v-btn>
                    </div>
                </v-col>
                <v-col cols="8">
                    <QuestionBuildWidget></QuestionBuildWidget>
                </v-col>
            </v-row>
        </v-content>
    </v-container>
</template>

<script>
import draggable from 'vuedraggable'
import QuestionBuildWidget from '../components/QuestionBuildWidget'

const axios = require('axios')
const instance = axios.create({
    baseURL: 'http://127.0.0.1:8088/api/',
    timeout: 1000,
    headers: { 'X-Custom-Header': 'foobar' },
})
export default {
    name: 'DragDropTest',
    components: { QuestionBuildWidget, draggable },
    data() {
        return {
            categories: [
                {
                    id: '1',
                    name: 'Hauptkategorie',
                },
            ],
        }
    },
    computed: {
        /** updateQuestionList() {
            return this.$store.state.questionOverview.questionText
        }, */
    },
    methods: {
        createCategory() {},
        sendData() {
            const obj = {
                pollId: '',
                questionMessage: this.test,
                answerPossibilites: [],
                questionType: '',
            }
            instance.post('/poll/' + this.pollId + '/addquestion', obj).catch()
        },
    },
}
</script>
