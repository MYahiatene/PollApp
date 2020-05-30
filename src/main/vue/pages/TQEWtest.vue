<template>
    <div>
        <v-card>
            <v-container>
                <v-row no-gutters>
                    <v-col>
                        <v-toolbar color="light-blue" dark>
                            <v-toolbar-title>DraggableBlocks</v-toolbar-title>
                            <v-spacer></v-spacer>
                        </v-toolbar>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col>
                        <text-question-evaluation-widget></text-question-evaluation-widget>
                    </v-col>
                </v-row>
            </v-container>
        </v-card>
    </div>
</template>

<script>
import TextQuestionEvaluationWidget from '../components/TextQuestionEvaluationWidget'
export default {
    name: 'TQEWtest',
    components: { TextQuestionEvaluationWidget },
}
</script>

<style scoped></style>
