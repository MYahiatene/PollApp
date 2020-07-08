<template>
    <!--    This widget includes the necessary controls to create a rangeQuestion-->
    <v-container>
        <v-row no-gutters>
            <v-text-field
                id="startValue"
                v-model="startValue"
                label="Beginne die Skala bei"
                type="number"
            ></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-text-field
                id="endValue"
                v-model="endValue"
                label="Ende der Skala bei"
                :value="startValue"
                type="number"
                :rules="endValueRules"
            ></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-text-field
                id="stepsize"
                v-model="stepValue"
                label="Schrittweite:"
                type="number"
                :rules="stepSizeRules"
            ></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-text-field label="Minimumbeschriftung" v-model="lowerText" hint="bspw. 'weniger als X'"></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-text-field label="Maximumbeschriftung" v-model="upperText" hint="bspw. 'mehr als X'"></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-switch v-model="hideValues" label="Werte ausblenden"></v-switch>
        </v-row>
        <!--<v-row no-gutters>
            <v-switch label="'Möchte ich nicht beantworten' erlauben" v-model="noAnswer"></v-switch>
        </v-row>-->
    </v-container>
</template>

<script>
// TODO: Forms vollenden (keine Priorität)
import { mapGetters, mapMutations } from 'vuex'

export default {
    name: 'SlideQuestion',
    props: { pollData: { type: Array }, questionData: { type: Object }, buildIndex: { type: Number } },
    data() {
        return {
            openLower: false,
            openUpper: false,
            valid: false,
            // rules that assert that the endValue entered in the textField must be higher than the starValue
            endValueRules: [
                (v) => {
                    if (parseFloat(v) > this.startValue) {
                        this.setSaveButtonStatus(true)
                    } else {
                        this.setSaveButtonStatus(false)
                        return 'Die Zahl muss größer als der Endwert sein!'
                    }
                },
            ],
            // rules that assert that the stepsize entered in the textField must be lower than the difference between starValue and endValue
            stepSizeRules: [
                (v) => {
                    if (
                        parseFloat(v) < this.endValue - this.startValue &&
                        (this.endValue - this.startValue) % parseFloat(v) === 0
                    ) {
                        this.setSaveButtonStatus(true)
                    } else if (parseFloat(v) > this.endValue - this.startValue) {
                        this.setSaveButtonStatus(false)
                        return 'Die Schrittweite muss kleiner als die Reichweite sein!'
                    } else {
                        this.setSaveButtonStatus(false)
                        return 'Die Reichweite muss durch die Schrittweite teilbar sein'
                    }
                },
            ],
        }
    },
    computed: {
        ...mapGetters({ getQuestion: 'questionOverview/getQuestion' }),
        startValue: {
            get() {
                return this.getQuestion.startValue
            },
            set(value) {
                this.setStart(value)
            },
        },
        endValue: {
            get() {
                return this.getQuestion.endValue
            },
            set(value) {
                this.setFinish(value)
            },
        },
        stepValue: {
            get() {
                return this.getQuestion.stepSize
            },
            set(value) {
                this.setStep(value)
            },
        },
        lowerText: {
            get() {
                return this.getQuestion.belowMessage
            },
            set(value) {
                this.setLower(value)
            },
        },
        upperText: {
            get() {
                return this.getQuestion.aboveMessage
            },
            set(value) {
                this.setUpper(value)
            },
        },
        hideValues: {
            get() {
                return this.getQuestion.hideValues
            },
            set(value) {
                this.setHideValues(value)
            },
        },
        /* noAnswer: {
                get() {
                    return this.getQuestion.intervalNoAnswer
                },
                set(value) {
                    this.setNA(value)
                },
            }, */
    },
    mounted() {
        this.openLower = this.getQuestion.belowMessage
        this.openUpper = this.getQuestion.aboveMessage
        // this.setSaveButtonStatus(true) ToDo: Figure out why this doesn't work
    },
    methods: {
        ...mapMutations({
            setSaveButtonStatus: 'questionOverview/setSaveButtonStatus',
            setStart: 'questionOverview/setStartValue',
            setFinish: 'questionOverview/setEndValue',
            setStep: 'questionOverview/setStepSize',
            setLower: 'questionOverview/setBelowMessage',
            setUpper: 'questionOverview/setAboveMessage',
            setHideValues: 'questionOverview/setHideValues',
            // setNA: 'pollOverview/setIntervalNoAnswer',
        }),
        clearLower() {
            if (!this.openLower) {
                this.setLower(null)
            }
        },
        clearUpper() {
            if (!this.openUpper) {
                this.setUpper(null)
            }
        },
    },
}
</script>

<style scoped></style>
