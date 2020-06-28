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
        ></v-date-picker>

        <p>Zeige Antworten, die</p>
        <p v-if="inverted">nicht</p>
        <p>in diesem Bereich getätigt wurden.</p>
        <p>
            {{ dates }}
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
}
</script>

<style scoped></style>
