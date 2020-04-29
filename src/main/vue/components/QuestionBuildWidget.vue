<!--this widget enables the user to configure a general question, including all of its common features, to specify-->
<!--the final question type and to preview (wysiwyg) the final question as it will be displayed to the participants-->
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
                        label="Fragentext"
                        hint="Was soll den Umfrageteilnehmer gefragt werden?"
                        rows="1"
                        v-model="questionText"
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
                    <component v-bind:is="currentComponent"></component>
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
