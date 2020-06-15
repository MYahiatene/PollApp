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
            <v-switch v-model="openLower" label="Offene unterer Grenze" @change="clearLower()"></v-switch>
        </v-row>
        <v-row v-if="openLower" no-gutters>
            <v-text-field label="Minimumbeschriftung" v-model="lowerText" hint="bspw. 'weniger als X'"></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-switch v-model="openUpper" label="Offene obere Grenze" @change="clearUpper()"></v-switch>
        </v-row>
        <v-row v-if="openUpper" no-gutters>
            <v-text-field label="Maximumbeschriftung" v-model="upperText" hint="bspw. 'mehr als X'"></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-switch label="'Möchte ich nicht beantworten' erlauben" v-model="noAnswer"></v-switch>
        </v-row>
    </v-container>
</template>

<script>
// TODO: Forms vollenden (keine Priorität)
import { mapGetters, mapMutations } from 'vuex'

export default {
    name: 'RangeQuestion',
    props: { pollData: { type: Array }, questionData: { type: Object }, buildIndex: { type: Number } },
    data() {
        return {
            openLower: false,
            openUpper: false,
            valid: false,
            // rules that assert that the endValue entered in the textField must be higher than the starValue
            endValueRules: [(v) => parseFloat(v) > this.startValue || 'Die Zahl muss größer als der Endwert sein!'],
            // rules that assert that the stepsize entered in the textField must be lower than the difference between starValue and endValue
            stepSizeRules: [
                (v) =>
                    parseFloat(v) < this.endValue - this.startValue ||
                    'Die Schrittweite muss kleiner als die Reichweite sein!',
                (v) =>
                    (this.endValue - this.startValue) % parseFloat(v, 10) === 0 ||
                    'Die Reichweite muss durch die Schrittweite teilbar sein',
            ],
        }
    },
    mounted() {
        this.openLower = this.getQuestion.intervalUpperText
        this.openUpper = this.getQuestion.intervalLowerText
    },
    methods: {
        ...mapMutations({
            setStart: 'pollOverview/setIntervalStart',
            setFinish: 'pollOverview/setIntervalFinish',
            setStep: 'pollOverview/setIntervalStep',
            setLower: 'pollOverview/setIntervalLowerText',
            setUpper: 'pollOverview/setIntervalUpperText',
            setNA: 'pollOverview/setIntervalNoAnswer',
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
    computed: {
        ...mapGetters({ getQuestion: 'pollOverview/getQuestion' }),
        startValue: {
            get() {
                return this.getQuestion.intervalStart
            },
            set(value) {
                this.setStart(value)
            },
        },
        endValue: {
            get() {
                return this.getQuestion.intervalFinish
            },
            set(value) {
                this.setFinish(value)
            },
        },
        stepValue: {
            get() {
                return this.getQuestion.intervalStep
            },
            set(value) {
                this.setStep(value)
            },
        },
        lowerText: {
            get() {
                return this.getQuestion.intervalLowerText
            },
            set(value) {
                this.setLower(value)
            },
        },
        upperText: {
            get() {
                return this.getQuestion.intervalUpperText
            },
            set(value) {
                this.setUpper(value)
            },
        },
        noAnswer: {
            get() {
                return this.getQuestion.intervalNoAnswer
            },
            set(value) {
                this.setNA(value)
            },
        },
    },
}
</script>

<style scoped></style>
