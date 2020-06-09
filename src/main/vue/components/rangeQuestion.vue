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
            <v-text-field id="stepsize" label="Schrittweite:" type="number" :rules="stepSizeRules"></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-switch v-model="openLower" label="Offene unterer Grenze"></v-switch>
        </v-row>
        <v-row v-if="openLower" no-gutters>
            <v-text-field label="Minimumbeschriftung" hint="bspw. 'weniger als X'"></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-switch v-model="openUpper" label="Offene obere Grenze"></v-switch>
        </v-row>
        <v-row v-if="openUpper" no-gutters>
            <v-text-field label="Maximumbeschriftung" hint="bspw. 'mehr als X'"></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-switch label="'Möchte ich nicht beantworten' erlauben"></v-switch>
        </v-row>
    </v-container>
</template>

<script>
export default {
    name: 'RangeQuestion',
    startValue: 0,
    endValue: 0,

    data() {
        return {
            openLower: false,
            openUpper: false,
            valid: false,
            // rules that assert that the endValue entered in the textField must be higher than the starValue
            endValueRules: [(v) => parseFloat(v, 10) > this.startValue || 'Die Zahl muss größer als der Endwert sein!'],
            // rules that assert that the stepsize entered in the textField must be lower than the difference between starValue and endValue
            stepSizeRules: [
                (v) =>
                    parseFloat(v, 10) < this.endValue - this.startValue ||
                    'Die Schrittweite muss kleiner als die Reichweite sein!',
                (v) =>
                    (this.endValue - this.startValue) % parseFloat(v, 10) === 0 ||
                    'Die Reichweite muss durch die Schrittweite teilbar sein',
            ],
        }
    },
}
</script>

<style scoped></style>
