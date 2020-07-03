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

                    <!--TextField for name of survey, is required and has to obey titleRules(see data)-->
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

                    <!--Pick anonimityType from anonymityTypes and has to be choosen(anonymityRules)-->
                    <v-row no-gutters>
                        <v-col cols="12" md="4">
                            <v-overflow-btn
                                v-model="selectedAnonymityType"
                                :items="anonymityTypes"
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

                    <!--Decide if participants can go back and forth between categories-->
                    <v-row no-gutters>
                        <v-switch
                            v-model="categoryChange"
                            label="Vor- und Zurückspringen zwischen Kategorien erlaubt"
                        ></v-switch>
                    </v-row>

                    <!--Decide, if number of questions should be visible-->
                    <v-row no-gutters>
                        <v-switch v-model="visibility" label="Sichtbarkeit der Gesamtanzahl der Fragen"></v-switch>
                    </v-row>

                    <!--Pick own design for poll-->
                    <v-row no-gutters>
                        <v-switch v-model="switch3" label="eigenes Design für die Umfrage"></v-switch>
                    </v-row>
                    <v-row v-if="switch3" no-gutters>
                        <!--Pick own backgroundColor and save it-->
                        <v-col cols="12" md="4">
                            Hintergrundfarbe
                            <v-color-picker @update:color="getBackgroundColor" />
                        </v-col>
                        <!--Pick own backgroundColor and save it-->
                        <v-col cols="12" md="4">
                            Schriftfarbe
                            <v-color-picker @update:color="getFontColor" />
                        </v-col>
                        <!--Pick a file for upload and logo is shown for verification-->
                        <v-col cols="12" md="4">
                            Logo hochladen
                            <input type="file" @change="showImage" />
                            <img :src="logo" alt="Image" width="350" />
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
import { mapGetters } from 'vuex'
export default {
    name: 'PollCreation',
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
            selectedAnonymityType: '',
            anonymityTypes: [
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
            titleRules: [(v) => !!v || 'Titel fehlt'],
            anonymityRules: [(v) => !!v || 'Anonymitätsgrad fehlt.'],
        }
    },
    computed: {
        ...mapGetters({
            isAuthenticated: 'login/isAuthenticated',
            getUsername: 'login/getUsername',
        }),
        time() {
            const today = new Date()
            return today.getHours() + ':' + today.getMinutes() + ':00'
        },
    },
    methods: {
        validate() {
            this.$refs.form.validate()
        },
        /**
         * Puts all information in an object and directly acces axios over above declared instance at PostMapping
         * createPoll in PollController.
         */
        sendData() {
            const obj = {
                pollCreator: this.getUsername,
                anonymityStatus: this.selectedAnonymityType,
                pollName: this.title,
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
            console.log(obj)
            this.$axios.post('/createpoll', obj).catch()
            this.$router.push('/polls')
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
        /**
         * Reads the given file and saves image as this.logo.
         * @param e Change-Event
         * */
        showImage(e) {
            const image = e.target.files[0]
            const reader = new FileReader()
            reader.readAsDataURL(image)
            reader.onload = (e) => {
                console.log(e)
                this.logo = e.target.result
            }
        },
        /**
         * Get's picked background color and saves it as this.backgroundColor.
         * @param e Change-Event
         */
        getBackgroundColor(e) {
            this.backgroundColor = e.hexa
        },
        /**
         * Get's picked font color and saves it as this.fontColor.
         * @param e Change-Event
         */
        getFontColor(e) {
            this.fontColor = e.hexa
        },
    },
}
</script>

<style scoped></style>
