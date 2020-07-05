<template>
    <div>
        <v-switch v-model="inverted" label="Invertieren" @change="update()"> </v-switch>
        <v-row>
            <v-col>
                <v-date-picker
                    :key="datePickerKey"
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
                <p>dem {{ startDisplay }}</p>
                <v-text-field
                    v-model="time1"
                    label="Uhrzeit"
                    type="time"
                    style="width: 170px;"
                    @change="update()"
                ></v-text-field>
                <p>und dem {{ endDisplay }}</p>
                <v-text-field
                    v-model="time2"
                    label="Uhrzeit"
                    type="time"
                    style="width: 170px;"
                    @change="update()"
                ></v-text-field>
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
        startDisplay: 'Start - Datum',
        endDisplay: 'End - Datum',
        time1: '00:00',
        time2: '23:59',
        datePickerKey: 0,
    }),
    props: {
        filterIndex: {
            type: Number,
        },
        startDate: {
            type: String,
            default: '2000-01-01 00:00',
        },
        endDate: {
            type: String,
            default: '2100-01-01 23:59',
        },
        invertFilter: {
            type: Boolean,
            default: false,
        },
    },

    mounted() {
        console.log('new dateFilter', this.filterIndex)
        const start = this.startDate
        const end = this.endDate
        this.dates[0] = start.substr(0, 10)
        this.dates[1] = end.substr(0, 10)
        this.time1 = this.startDate.substr(11, 5)
        this.time2 = this.endDate.substr(11, 5)
        this.inverted = this.invertFilter
        this.computeDates()
        this.$forceUpdate()
        this.datePickerKey += 1
        console.log(this.dates)
    },

    computed: {
        conditional() {
            if (this.inverted) {
                return 'NICHT'
            } else {
                return ''
            }
        },
    },

    methods: {
        update() {
            this.time1 = this.time1.length === 0 ? '00:00' : this.time1
            this.time2 = this.time2.length === 0 ? '23:59' : this.time2
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
                if (this.dates[0] + ' ' + this.time1 > this.dates[1] + ' ' + this.time2) {
                    console.log('wechsel')
                    this.startDisplay = this.dates[1]
                    this.endDisplay = this.dates[0]
                    const tmp = this.time1
                    this.time1 = this.time2
                    this.time2 = tmp
                } else {
                    this.startDisplay = this.dates[0]
                    this.endDisplay = this.dates[1]
                }
            }
        },
    },
}
</script>

<style scoped></style>
