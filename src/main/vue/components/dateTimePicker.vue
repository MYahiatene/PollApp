<template>
    <v-container>
        <v-row>
            <v-col cols="12" lg="6">
                <v-menu
                    ref="menu1"
                    v-model="menu1"
                    :close-on-content-click="false"
                    transition="scale-transition"
                    offset-y
                    max-width="290px"
                    min-width="290px"
                >
                    <template v-slot:activator="{ on }">
                        <v-text-field
                            v-model="dateFormatted"
                            label="Datum"
                            persistent-hint
                            @blur="date = parseDate(dateFormatted)"
                            v-on="on"
                        ></v-text-field>
                    </template>
                    <v-date-picker
                        v-model="date"
                        no-title
                        @input="menu1 = false"
                        show-current="true"
                        :min="new Date().toISOString().substr(0, 10)"
                        max="2100-01-01"
                    ></v-date-picker>
                </v-menu>
            </v-col>
            <v-col><v-text-field label="Uhrzeit" :value="time" type="time" style="width: 170px;"></v-text-field></v-col>
        </v-row>
    </v-container>
</template>

<script>
export default {
    name: 'dateTimePicker',
    data: (vm) => ({
        date: new Date().toISOString().substr(0, 10),
        dateFormatted: vm.formatDate(new Date().toISOString().substr(0, 10)),
        menu1: false,
        menu2: false,
    }),
    computed: {
        time() {
            const today = new Date()
            return today.getHours() + ':' + today.getMinutes() + ':00'
        },
    },

    watch: {
        date(val) {
            this.dateFormatted = this.formatDate(this.date)
        },
    },

    methods: {
        formatDate(date) {
            if (!date) return null

            const [year, month, day] = date.split('-')
            return `${month}/${day}/${year}`
        },
        parseDate(date) {
            if (!date) return null

            const [month, day, year] = date.split('/')
            return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`
        },
    },
}
</script>

<style scoped></style>
