<template>
    <v-container grid-list-md text-xs-center>
        <v-layout column wrap>
            <div class="row">
                <div class="col-12">
                    <draggable
                        v-model="blocks"
                        tag="v-layout"
                        class="column wrap fill-height fill-width align-center sortable-list"
                        style="background: grey;"
                    >
                        <v-flex
                            v-for="row in blocks"
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
                                <v-flex v-for="block in row.items" :key="block.title" fluid class="row-v">
                                    <!--xs4 pa-3-->
                                    <QuestionFilterBlock
                                        v-if="block.type === 'qf'"
                                        :questions="questions"
                                        :question-set="block.targetQuestion"
                                        :inverted="block.inverted"
                                    ></QuestionFilterBlock>
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
import QuestionFilterBlock from './QuestionFilterBlock'

export default {
    questionSet: '',
    answerList: [],
    name: 'Functional',
    display: 'Functional third party',
    order: 17,
    components: {
        QuestionFilterBlock,
        draggable,
    },
    data() {
        return {
            currentIndex: 4,
            blocks: [
                {
                    index: 1,
                    items: [
                        { type: 'qf', targetQuestion: 'Frage 1', selectedAnswers: ['Ja', 'Egal'], inverted: false },
                    ],
                },
                {
                    index: 2,
                    items: [{ type: 'qf', targetQuestion: 'Frage 2', selectedAnswers: ['Alt'], inverted: true }],
                },
                {
                    index: 3,
                    items: [
                        {
                            type: 'qf',
                            targetQuestion: 'Frage 4',
                            selectedAnswers: ['20', '30', '40', '50'],
                            inverted: false,
                        },
                    ],
                },
            ],
            questions: [
                { fragentext: 'Frage 1', answers: ['Ja', 'Nein', 'Egal'] },
                { fragentext: 'Frage 2', answers: ['Alt', 'Vielleicht'] },
                { fragentext: 'Frage 3', answers: ['Gut', 'Sehr gut'] },
                {
                    fragentext: 'Frage 4',
                    answers: ['unter 10', '10', '20', '30', '40', '50', '60', '70', '80', 'über 80'],
                },
            ],
            addFilter() {
                this.blocks.push({ index: this.currentIndex, items: [{ type: 'qf' }] })
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
