<template>
    <v-container>
        <v-row no-gutters>
            <v-text-field
                v-model="startValue"
                label="Beginne die Skala bei"
                id="startValue"
                type="number"
            ></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-text-field
                v-model="endValue"
                label="Ende der Skala bei"
                :value="this.startValue"
                id="endValue"
                type="number"
                :rules="endValueRules"
            ></v-text-field>
        </v-row>
        <!--Schrittweite wirklich nötig?-->
        <v-row no-gutters>
            <v-text-field label="Schrittweite:" id="stepsize" type="number" :rules="stepSizeRules"></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-switch label="Offene unterer Grenze" v-model="openLower"></v-switch>
        </v-row>
        <v-row no-gutters v-if="openLower">
            <v-text-field label="Minimumbeschriftung" hint="bspw. 'weniger als X'"></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-switch label="Offene obere Grenze" v-model="openUpper"></v-switch>
        </v-row>
        <v-row no-gutters v-if="openUpper">
            <v-text-field label="Maximumbeschriftung" hint="bspw. 'mehr als X'"></v-text-field>
        </v-row>
        <v-row no-gutters>
            <v-switch label="'Möchte ich nicht beantworten' erlauben"></v-switch>
        </v-row>
    </v-container>
</template>

<script>
export default {
    name: 'rangeQuestion',
    startValue: 0,
    endValue: 0,

    data() {
        return {
            openLower: false,
            openUpper: false,
            valid: false,
            endValueRules: [(v) => parseFloat(v, 10) > this.startValue || 'Die Zahl muss größer als der Endwert sein!'],
            stepSizeRules: [
                (v) =>
                    parseFloat(v, 10) < this.endValue - this.startValue ||
                    'Die Schrittweite muss kleiner als die Reichweite sein!',
            ],
        }
    },
}
</script>

<style scoped></style>
