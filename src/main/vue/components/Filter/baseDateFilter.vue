<template>
    <div>
        <v-switch v-model="inverted" label="Invertieren"> </v-switch>
        <v-row>
            <v-col>
                <v-date-picker
                    id="datePicker"
                    range
                    v-model="dates"
                    no-title
                    show-current="true"
                    locale="de"
                    :min="min"
                    :max="max"
                    @change="computeDates"
                    @input="update()"
                ></v-date-picker>
            </v-col>
            <v-col>
                <p>Zeige Antworten, die {{ conditional }} zwischen</p>
                <p>dem {{ startDate }}</p>
                <v-text-field label="Uhrzeit" v-model="time1" type="time" style="width: 170px;"></v-text-field>
                <p>und dem {{ endDate }}</p>
                <v-text-field label="Uhrzeit" v-model="time2" type="time" style="width: 170px;"></v-text-field>
                <p>getätigt wurden.</p>
            </v-col>
        </v-row>
    </div>
</template>

<script>
export default {
    name: 'baseDateFilter',
    data: (vm) => ({
        date: new Date().toISOString().substr(0, 10),
        dates: [],
        min: '2000-01-01',
        max: '2100-01-01',
        inverted: false,
        startDate: 'Start - Datum',
        endDate: 'End - Datum',
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
                this.$emit('newDateFilter', [
                    this.filterIndex,
                    this.dates[0] + ' ' + this.time1,
                    this.dates[1] + ' ' + this.time2,
                    this.inverted,
                ])
            }
        },

        computeDates() {
            console.log('dates...:')
            console.log(this.dates)
            if (this.dates[0] && this.dates[1]) {
                console.log('im if')
                console.log(this.dates[0].substring(8, 9))
                console.log(this.dates[1].substring(8, 9))
                let datesSwitched = false
                if (parseInt(this.dates[0].substring(0, 4), 10) > parseInt(this.dates[1].substring(0, 4), 10)) {
                    console.log('Dates Switched at year')
                    datesSwitched = true
                } else if (parseInt(this.dates[0].substring(5, 7), 10) > parseInt(this.dates[1].substring(5, 7), 10)) {
                    console.log('Dates Switched at month')
                    datesSwitched = true
                } else if (
                    parseInt(this.dates[0].substring(8, 10), 10) > parseInt(this.dates[1].substring(8, 10), 10)
                ) {
                    console.log('Dates Switched at day')

                    datesSwitched = true
                }

                if (datesSwitched) {
                    this.startDate = this.dates[1]
                    this.endDate = this.dates[0]
                } else {
                    this.startDate = this.dates[0]
                    this.endDate = this.dates[1]
                }
            } else {
                console.log('in else')
                this.startDate = 'Start-Datum'
                this.endDate = 'End-Datum'
                this.$forceUpdate()
            }
        },
    },
}
</script>

<style scoped></style>
