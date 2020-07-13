<template>
    <!--    this widget includes the necessary controls to create a textQuestion-->
    <v-container>
        <v-form v-model="valid">
            <v-row no-gutters>
                <v-switch v-model="newline" label="Mehrzeilige Antworten erlauben"></v-switch>
            </v-row>
            <v-row no-gutters>
                <v-switch
                    v-model="minCharSwitch"
                    label="Zeichenanzahl nach unten beschränken"
                    @change="clearMin"
                ></v-switch>
            </v-row>
            <!--            textField to specify the lowest possible number of chars-->
            <v-row v-if="minCharSwitch">
                <v-col md="5">
                    <v-text-field
                        single-line
                        type="number"
                        v-model="minimum"
                        min="0"
                        prefix="mindestens"
                        suffix=" Zeichen gefordert"
                        :rules="minRules"
                    ></v-text-field>
                </v-col>
            </v-row>
            <v-row no-gutters>
                <v-switch
                    v-model="maxCharSwitch"
                    label="Zeichenanzahl nach oben beschränken"
                    @change="clearMax"
                ></v-switch>
            </v-row>
            <!--            textField to specify the highest possible number of chars-->
            <v-row v-if="maxCharSwitch">
                <v-col md="5">
                    <v-text-field
                        single-line
                        type="number"
                        v-model="maximum"
                        min="1"
                        value="100"
                        prefix="maximal"
                        suffix=" Zeichen erlaubt"
                        :rules="maxRules"
                    ></v-text-field>
                </v-col>
            </v-row>
        </v-form>
    </v-container>
</template>

<script>
// TODO: Forms vollenden
import { mapGetters, mapMutations } from 'vuex'

export default {
    name: 'TextQuestion',
    props: { pollData: { type: Object }, questionData: { type: Object }, buildIndex: { type: Number } },
    data() {
        return {
            valid: false,
            // rules that assert that the input in the textField must be positive (there can't be a negative number of chars on an answer
            minRules: [
                (v) => {
                    if (v <= this.maximum && v > 0) {
                        this.setSaveButtonStatus(true)
                        return ''
                    } else if (v < 1) {
                        this.setSaveButtonStatus(false)
                        return 'Die Zahl muss größer als 0 sein!'
                    } else {
                        this.setSaveButtonStatus(false)
                        return 'Die Zahl muss kleiner als das Maximum sein!'
                    }
                },
            ],
            maxRules: [
                (v) => {
                    if (v >= this.minimum && v > 0) {
                        this.setSaveButtonStatus(true)
                        return ''
                    } else if (v < 1) {
                        this.setSaveButtonStatus(false)
                        return 'Die Zahl muss größer als 0 sein!'
                    } else {
                        this.setSaveButtonStatus(false)
                        return 'Die Zahl muss größer als das Minimum sein!'
                    }
                },
            ],
        }
    },
    methods: {
        ...mapMutations({
            setSaveButtonStatus: 'questionOverview/setSaveButtonStatus',
            multiline: 'questionOverview/setTextMultiline',
            setMin: 'questionOverview/setTextMin',
            setMax: 'questionOverview/setTextMax',
            setMinCharSwitch: 'questionOverview/setTextMinBool',
            setMaxCharSwitch: 'questionOverview/setTextMaxBool',
        }),
        clearMin() {
            if (!this.minCharSwitch) {
                this.setMin(0)
            }
        },
        clearMax() {
            if (!this.maxCharSwitch) {
                this.setMax(10000)
            }
        },
    },
    computed: {
        ...mapGetters({ getQuestion: 'questionOverview/getQuestion' }),
        newline: {
            get() {
                return this.getQuestion.textMultiline
            },
            set(value) {
                this.multiline(value)
            },
        },
        minimum: {
            get() {
                return this.getQuestion.textMinimum
            },
            set(value) {
                this.setMin(parseInt(value))
            },
        },
        maximum: {
            get() {
                return this.getQuestion.textMaximum
            },
            set(value) {
                this.setMax(parseInt(value))
            },
        },
        minCharSwitch: {
            get() {
                return this.getQuestion.textMinBool
            },
            set(value) {
                this.setMinCharSwitch(value)
            },
        },
        maxCharSwitch: {
            get() {
                return this.getQuestion.textMaxBool
            },
            set(value) {
                this.setMaxCharSwitch(value)
            },
        },
    },
}
</script>

<style scoped></style>
