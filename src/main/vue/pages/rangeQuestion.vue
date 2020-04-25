<template>
    <div>
        <v-text-field
            v-model="startValue"
            prefix="Beginne die Skala bei"
            id="startValue"
            single-line
            type="number"
            style="width: 200px;"
        ></v-text-field>
        <v-text-field
            v-model="endValue"
            prefix="Ende die Skala bei"
            :value="this.startValue"
            id="endValue"
            single-line
            type="number"
            style="width: 200px;"
            :rules="endValueRules"
        ></v-text-field>
        <v-text-field
            prefix="Schrittweite:"
            id="stepsize"
            single-line
            type="number"
            style="width: 200px;"
            :rules="stepSizeRules"
        ></v-text-field>
    </div>
</template>

<script>
export default {
    name: 'rangeQuestion',
    startValue: 0,
    endValue: 0,

    data() {
        return {
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
