<template>
    <div>
        <v-switch v-model="inverted" label="Invertieren"> </v-switch>
        <v-date-picker
            id="datePicker"
            range
            v-model="dates"
            no-title
            show-current="true"
            locale="de"
            :min="min"
            :max="max"
            @input="$emit('newDateFilter', [filterIndex, dates[0], dates[1], inverted])"
        ></v-date-picker>

        <p>Zeige Antworten, die</p>
        <p v-if="inverted">nicht</p>
        <p>in diesem Bereich getätigt wurden.</p>
        <p>
            {{ nicelyFormatedDates }}
        </p>
    </div>
</template>

<script>
export default {
    name: 'baseDateFilter',
    data: (vm) => ({
        date: new Date().toISOString().substr(0, 10),
        dates: [],
        min: new Date().toISOString().substr(0, 10),
        max: '2100-01-01',
        inverted: false,
    }),
    props: {
        filterIndex: {
            type: Number,
        },
    },

    // computed(){
    //
    //     startDate(){
    //         dates
    //     }
    //
    //     nicelyFormatedDates(){
    //
    //         if (startDate && endDate) {
    //             for (let i = 0; i < startDate.length; i++) {
    //                 if (!(startDate.charAt(i) === '-')) {
    //                     if (parseInt(startDate.charAt(i), 10) > parseInt(endDate.charAt(i), 10)) {
    //                         datesSwitched = true
    //                     }
    //                 }
    //             }
    //         }
    //     }
    //
    // }
}
</script>

<style scoped></style>
