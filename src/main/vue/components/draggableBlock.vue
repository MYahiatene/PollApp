<template>
    <v-container grid-list-md text-xs-center>
        <v-layout column wrap>
            <div class="row">
                <div class="col-12">
                    <draggable
                        v-model="rows"
                        tag="v-layout"
                        class="column wrap fill-height fill-width align-center sortable-list"
                        style="background: grey;"
                    >
                        <v-flex
                            v-for="row in rows"
                            :key="row.index"
                            class="sortable"
                            xs12
                            my-2
                            style="background: #f46400;"
                        >
                            <draggable
                                :list="row.items"
                                tag="v-layout"
                                :group="{ name: 'row' }"
                                class="column wrap justify-space-around"
                            >
                                <v-flex v-for="item in row.items" :key="item.title" xs4 pa-3 class="row-v">
                                    <!--xs4 pa-3-->
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
                                </v-flex>
                            </draggable>
                        </v-flex>
                    </draggable>
                </div>

                <!--<rawDisplayer class="col-3" :value="rows" title="List" />-->
            </div>
            <v-btn color="primary" @click="addFilter()">Filter hinzufügen</v-btn>
        </v-layout>
    </v-container>
</template>

<script>
import draggable from 'vuedraggable'
import 'vuetify/dist/vuetify.min.css'

export default {
    questionSet: '',
    answerList: [],
    name: 'Functional',
    display: 'Functional third party',
    order: 17,
    components: {
        draggable,
    },
    data() {
        return {
            currentIndex: 4,
            rows: [
                {
                    index: 1,
                    items: [
                        {
                            title: 'Geschlecht',
                        },
                    ],
                },
                {
                    index: 2,
                    items: [
                        {
                            title: 'Alter',
                        },
                    ],
                },
                {
                    index: 3,
                    items: [
                        {
                            title: 'Meinung über Karl Marx',
                        },
                    ],
                },
            ],
            questions: [
                { title: 'Frage 1', answers: ['Ja', 'Egal'] },
                { title: 'Frage 2', answers: ['Alt', 'Vielleicht'] },
                { title: 'Frage 3', answers: ['Gut', 'Sehr gut'] },
            ],
            questions2: ['Frage1', 'Frage2', 'Frage3'],
            enabled: true,
            changeQuestionID() {
                this.answerList = this.questions[this.questions2.indexOf(this.questionSet)].answers
                this.$forceUpdate()
            },
            addFilter() {
                this.rows.push({ index: this.currentIndex, items: [{ title: 'Theo Waigel-Augenbrauen?' }] })
                this.questions.push({ title: 'Frage'.concat(this.currentIndex), answers: ['Ja', 'Egal'] })
                this.questions2.push('Frage'.concat(this.currentIndex))
                this.currentIndex++
                this.$forceUpdate()
            },
        }
    },
}
</script>
<style scoped>
.buttons {
    margin-top: 35px;
}
/*.row-v {
    height: 150px;
    width: 200px;
}*/
.ghost {
    opacity: 0.5;
    background: #c8ebfb;
}
</style>
