<template>
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
                :value="this.startValue"
                type="number"
                :rules="endValueRules"
            ></v-text-field>
        </v-row>
        <!--Schrittweite wirklich nötig?-->
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
