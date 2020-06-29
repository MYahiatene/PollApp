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
            @input="update()"
        ></v-date-picker>

        <p>Zeige Antworten, die {{ conditional }} zwischen dem {{ startDate }} {{ time1 }}</p>
        <v-text-field label="Uhrzeit" :value="time1" type="time" style="width: 170px;"></v-text-field>
        <p>und dem {{ endDate }} {{ time2 }}</p>
        <v-text-field label="Uhrzeit" :value="time2" type="time" style="width: 170px;"></v-text-field>
        <p>getätigt wurden.</p>
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
        startDate: '',
        endDate: '',
        time1: '',
        time2: '',
    }),
    props: {
        filterIndex: {
            type: Number,
        },
    },

    computed: {
        conditional() {
            if (this.inverted) {
                return 'nicht'
            } else {
                return ''
            }
        },
    },

    methods: {
        update() {
            if (this.dates.length === 2) {
                this.computeDates()
                this.$emit('newDateFilter', [this.filterIndex, this.dates[0], this.dates[1], this.inverted])
            }
        },

        computeDates() {
            let datesSwitched = false

            if (this.dates[0] && this.dates[1]) {
                if (parseInt(this.dates[0].substring(0, 3), 10) > parseInt(this.dates[1].substring(0, 4), 10)) {
                    console.log('Dates Switched at year')
                    datesSwitched = true
                } else if (parseInt(this.dates[0].substring(5, 6), 10) > parseInt(this.dates[1].substring(5, 6), 10)) {
                    console.log('Dates Switched at month')
                    datesSwitched = true
                } else if (parseInt(this.dates[0].substring(8, 9), 10) > parseInt(this.dates[1].substring(8, 9), 10)) {
                    console.log('Dates Switched at day')
                    datesSwitched = true
                }
            }

            if (datesSwitched) {
                this.startDate = this.dates[1]
                this.endDate = this.dates[0]
            } else {
                this.startDate = this.dates[0]
                this.endDate = this.dates[1]
            }
        },
    },
}
</script>

<style scoped></style>
