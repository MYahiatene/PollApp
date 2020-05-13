<template>
    <div>
        <v-card>
            <v-container>
                <v-row no-gutters>
                    <v-col>
                        <h3>Frage erstellen</h3>
                    </v-col>
                </v-row>
                <v-row no-gutters>
                    <v-textarea
                        v-model="questionText"
                        label="Fragentext"
                        hint="Was soll den Umfrageteilnehmer gefragt werden?"
                        rows="1"
                    >
                    </v-textarea>
                </v-row>
                <v-row no-gutters>
                    <v-overflow-btn
                        v-model="questionChoice"
                        :items="questionTypes"
                        label="Fragenart"
                        @change="changeQuestionType()"
                    ></v-overflow-btn>
                </v-row>
                <v-row>
                    <component :is="currentComponent"></component>
                </v-row>
                <v-row no-gutters>
                    <h1>*preview wenn fertig*</h1>
                </v-row>
            </v-container>
        </v-card>
    </div>
</template>

<script>
import ChoiceQuestionConfigWidget from './ChoiceQuestionConfigWidget'
import TextQuestion from './textQuestion'
import RangeQuestion from './rangeQuestion'

export default {
    name: 'QuestionBuildWidget',
    components: { ChoiceQuestionConfigWidget, TextQuestion, RangeQuestion },
    data() {
        return {
            questionText: '',
            currentComponent: '',
            questionChoice: '',
            allComponents: ['ChoiceQuestionConfigWidget', 'TextQuestion', 'RangeQuestion'],
            questionTypes: ['Auswahlfrage', 'Freitextfrage', 'Intervallfrage'],
            changeQuestionType() {
                this.currentComponent = this.allComponents[this.questionTypes.indexOf(this.questionChoice)]
            },
        }
    },
}
</script>

<style scoped></style>
