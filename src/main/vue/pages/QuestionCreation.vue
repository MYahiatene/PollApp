<template>
    <v-card>
        <v-container>
            <v-form v-model="valid">
                <v-container>
                    <v-row no-gutters>
                        <v-col>
                            <h3>Umfrage erstellen</h3>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col cols="12" md="4">
                            <v-text-field
                                v-model="title"
                                hint="Wie soll die Umfrage genannt werden?"
                                :rules="titleRules"
                                label="Titel"
                                required
                            ></v-text-field>
                        </v-col>
                    </v-row>
                    <v-row no-gutters>
                        <v-col cols="12" md="4">
                            <v-overflow-btn
                                v-model="selectedAnonymityType"
                                :items="anonomityTypes"
                                placeholder="Anonymitätsgrad"
                                :rules="anonymityRules"
                            ></v-overflow-btn>
                        </v-col>
                    </v-row>
                    <v-row no-gutters>
                        <v-switch v-model="switch1" label="lege Aktivierungsdatum fest"></v-switch>
                    </v-row>

                    <v-row v-if="switch1" no-gutters>
                        <v-col cols="12" md="4">
                            <date-time-picker />
                        </v-col>
                    </v-row>

                    <v-row no-gutters>
                        <v-switch v-model="switch2" label="lege Deaktivierungsdatum fest"></v-switch>
                    </v-row>
                    <v-row v-if="switch2" no-gutters>
                        <v-col cols="12" md="4">
                            <date-time-picker />
                        </v-col>
                    </v-row>
                </v-container>

                <br />

                <v-btn :disabled="!valid" color="success" class="mr-4" @click="sendData()">Erstellen</v-btn>
            </v-form>
        </v-container>
    </v-card>
</template>

<script>
    import DateTimePicker from '../components/dateTimePicker'

    const axios = require('axios')
const instance = axios.create({
    baseURL: 'http://127.0.0.1:8088/api/',
    timeout: 1000,
    headers: { 'X-Custom-Header': 'foobar' },
})
export default {
    name: 'QuestionCreation',
    components: { DateTimePicker },
    data() {
        return {
            switch1: false,
            switch2: false,
            valid: false,
            title: '',
            selectedAnonymityType: '',
            anonomityTypes: [
                {
                    text: 'Anonym',
                    value: 1,
                },
                {
                    text: 'Teilanonym',
                    value: 2,
                },
                {
                    text: 'Nicht anonym',
                    value: 3,
                },
            ],
            titleRules: [(v) => !!v || 'Titel fehlt', (v) => v.length <= 10 || 'Name must be less than 10 characters'],
            anonymityRules: [(v) => !!v || 'Anonymitätsgrad fehlt.'],
        }
    },
    methods: {
        validate() {
            this.$refs.form.validate()
        },
        sendData() {
            const obj = {
                pollcreator: 'Richie',
                anonymityStatus: this.selectedAnonymityType,
                pollname: this.title,
                pollStatus: 0,
            }
            instance
                .post('/createpoll', obj)
                .then((response) => {
                    console.log(response)
                })
                .catch((error) => {
                    console.log(error)
                })
        },
    },
}
</script>

<style scoped></style>
