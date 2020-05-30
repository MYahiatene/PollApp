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
                                v-model="anonymityTypes"
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
                        <v-col cols="12" lg="2">
                            <v-menu
                                ref="menu"
                                v-model="menu"
                                :close-on-content-click="false"
                                transition="scale-transition"
                                offset-y
                                max-width="290px"
                                min-width="290px"
                            >
                                <template v-slot:activator="{ on }">
                                    <v-text-field
                                        v-model="activateDate"
                                        label="Datum"
                                        persistent-hint
                                        @blur="date = parseDate(activateDate)"
                                        v-on="on"
                                    ></v-text-field>
                                </template>
                                <v-date-picker
                                    id="datePicker"
                                    v-model="date"
                                    no-title
                                    show-current="true"
                                    locale="de"
                                    :min="new Date().toISOString().substr(0, 10)"
                                    max="2100-01-01"
                                    @input="closeMenuAndUpdateA"
                                ></v-date-picker>
                            </v-menu>
                        </v-col>
                        <v-col
                            ><v-text-field
                                label="Uhrzeit"
                                :value="time"
                                type="time"
                                style="width: 170px;"
                            ></v-text-field
                        ></v-col>
                    </v-row>

                    <v-row no-gutters>
                        <v-switch v-model="switch2" label="lege Deaktivierungsdatum fest"></v-switch>
                    </v-row>
                    <v-row v-if="switch2" no-gutters>
                        <v-col cols="12" md="2">
                            <v-menu
                                ref="menu"
                                v-model="menu"
                                :close-on-content-click="false"
                                transition="scale-transition"
                                offset-y
                                max-width="290px"
                                min-width="290px"
                            >
                                <template v-slot:activator="{ on }">
                                    <v-text-field
                                        v-model="deactivateDate"
                                        label="Datum"
                                        persistent-hint
                                        @blur="date = parseDate(deactivateDate)"
                                        v-on="on"
                                    ></v-text-field>
                                </template>
                                <v-date-picker
                                    id="datePicker"
                                    v-model="date"
                                    no-title
                                    show-current="true"
                                    locale="de"
                                    :min="getActivationDate(activateDate)"
                                    max="2100-01-01"
                                    @input="closeMenuAndUpdateD"
                                ></v-date-picker>
                            </v-menu>
                        </v-col>
                        <v-col
                            ><v-text-field
                                label="Uhrzeit"
                                :value="time"
                                type="time"
                                style="width: 170px;"
                            ></v-text-field
                        ></v-col>
                    </v-row>

                    <v-row no-gutters>
                        <v-switch
                            v-model="categoryChange"
                            label="Vor- und Zurückspringen zwischen Kategorien erlaubt"
                        ></v-switch>
                    </v-row>

                    <v-row no-gutters>
                        <v-switch v-model="visibility" label="Sichtbarkeit der Gesamtanzahl der Fragen"></v-switch>
                    </v-row>

                    <v-row no-gutters>
                        <v-switch v-model="switch3" label="eigenes Design für die Umfrage"></v-switch>
                    </v-row>
                    <v-row v-if="switch3" no-gutters>
                        <v-col cols="12" md="4">
                            Hintergrundfarbe
                            <v-color-picker @change="getBackgroundColor" />
                        </v-col>
                        <v-col cols="12" md="4">
                            Schriftfarbe
                            <v-color-picker @change="getFontColor" />
                        </v-col>
                        <v-col cols="12" md="4">
                            Logo hochladen
                            <input type="file" @change="showImage" />
                            <img :src="logo" alt="Image" width="500" />
                        </v-col>
                    </v-row>
                </v-container>

                <br />
                <!-- :disabled="!valid" -->
                <v-btn color="accent" class="mr-4" @click="sendData()" nuxt to="/PollEditing">Erstellen</v-btn>
            </v-form>
        </v-container>
    </v-card>
</template>

<script>
const axios = require('axios')
const instance = axios.create({
    baseURL: 'http://127.0.0.1:8088/api/',
    timeout: 1000,
    headers: {
        Authorization:
            'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZWN1cmUtYXBpIiwiYXVkIjoic2VjdXJlLWFwcCIsInN1YiI6InRicmV0dG1hbm4iLCJleHAiOjE1OTExMTAzMzQsInJvbCI6WyJST0xFX1BPTExfQ1JFQVRPUiJdfQ.Q6A2ST5I5Ix8_8jfsgxc3ZQq9GG7i88w_bJPlfEYA-QiAavpUhPbjFoUQWd9vZ93Xqzvm4oCw23bJ1NGtp2ucw',
        'X-Custom-Header': 'foobar',
    },
})
export default {
    name: 'QuestionCreation',
    components: {},
    data() {
        return {
            switch1: false,
            switch2: false,
            switch3: false,
            categoryChange: false,
            visibility: false,
            valid: false,
            title: '',
            backgroundColor: null,
            fontColor: null,
            logo: null,
            activateDate: this.formatDate(new Date().toISOString().substr(0, 10)),
            deactivateDate: this.formatDate(new Date().toISOString().substr(0, 10)),
            creationDate: this.formatDate(new Date().toISOString().substr(0, 10)),
            date: new Date().toISOString().substr(0, 10),
            menu: false,
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
    computed: {
        time() {
            const today = new Date()
            return today.getHours() + ':' + today.getMinutes() + ':00'
        },
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
                pollCreatedAt: this.creationDate,
                activatedAt: this.activateDate,
                deactivatedAt: this.deactivateDate,
                pollStatus: 0,
                categoryChange: this.categoryChange,
                visibility: this.visibility,
                backgroundColor: this.backgroundColor,
                fontColor: this.fontColor,
                logo: this.logo,
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
        formatDate(date) {
            if (!date) return null
            const [year, month, day] = date.split('-')
            return `${day}.${month}.${year}`
        },
        parseDate(date) {
            if (!date) return null
            const [day, month, year] = date.split('.')
            return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`
        },
        unparseDate(date) {
            if (!date) return new Date().toISOString.substr(0, 10)
            const [year, month, day] = date.split('-')
            return `${day.padStart(2, '0')}.${month.padStart(2, '0')}.${year}`
        },
        getActivationDate(date) {
            if (date === null) return new Date().toISOString.substr(0, 10)
            const [day, month, year] = date.split('.')
            return `${year}-${month}-${day}`
        },
        reformatActivateDate() {
            this.activateDate = this.formatDate(this.date)
        },
        reformatDeactivateDate() {
            this.deactivateDate = this.formatDate(this.date)
        },
        parseTime(date) {
            if (!date) return null
            return date.getHours() + ':' + date.getMinutes()
        },
        closeMenuAndUpdateA() {
            this.menu = false
            this.reformatActivateDate()
        },
        closeMenuAndUpdateD() {
            this.menu = false
            this.reformatDeactivateDate()
        },
        // Shows the uploaded logo for verification
        showImage(e) {
            const image = e.target.files[0]
            const reader = new FileReader()
            reader.readAsDataURL(image)
            reader.onload = (e) => {
                console.log(e)
                this.logo = e.target.result
            }
        },
        getBackgroundColor(e) {
            this.backgroundColor = e.payload[0].hexa
        },
        getFontColor(e) {
            this.fontColor = e.payload[0].hexa
        },
    },
}
</script>

<style scoped></style>
