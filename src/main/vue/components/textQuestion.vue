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
                        :rules="charRules"
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
                        :rules="charRules"
                    ></v-text-field>
                </v-col>
            </v-row>
        </v-form>
    </v-container>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'

export default {
    name: 'TextQuestion',
    data() {
        return {
            minCharSwitch: false,
            maxCharSwitch: false,
            valid: false,
            // rules that assert that the input in the textField must be positive (there can't be a negative number of chars on an answer
            charRules: [(v) => v >= 0 || 'Die Zahl muss positiv sein!'],
        }
    },
    mounted() {
        this.minCharSwitch = this.getQuestion.textMinimum !== null
        this.maxCharSwitch = this.getQuestion.textMaximum !== null
    },
    methods: {
        ...mapMutations({
            multiline: 'pollOverview/setTextMultiline',
            setMin: 'pollOverview/setTextMin',
            setMax: 'pollOverview/setTextMax',
        }),
        clearMin() {
            if (!this.minCharSwitch) {
                this.setMin(null)
            }
        },
        clearMax() {
            if (!this.maxCharSwitch) {
                this.setMax(null)
            }
        },
    },
    computed: {
        ...mapGetters({ getQuestion: 'pollOverview/getQuestion' }),
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
    },
}
</script>

<style scoped></style>
