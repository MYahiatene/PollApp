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
                                :label="pollTitle"
                                :rules="titleRules"
                                required
                            >
                            </v-text-field>
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
                                v-model="activatedTime"
                                label="Uhrzeit"
                                type="time"
                                style="width: 170px;"
                                @change="saveTime($event, 'a')"
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
                                v-model="deactivatedTime"
                                label="Uhrzeit"
                                type="time"
                                style="width: 170px;"
                                @change="saveTime($event, 'd')"
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
                <v-btn :disabled="!valid" color="success" class="mr-4" @click="goHome()">Erstellen</v-btn>
                <v-btn :disabled="!valid" color="success" class="mr-4" @click="goToNewPoll()"
                    >Erstellen und bearbeiten</v-btn
                >
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
            pollId: 0,
            categoryList: [],
            switch1: false,
            switch2: false,
            switch3: false,
            categoryChange: false,
            visibility: false,
            valid: false,
            title: '',
            pollTitle: 'Titel',
            backgroundColor: null,
            fontColor: null,
            logo: null,
            newPollId: -1,
            activateDate: this.formatDate(new Date().toISOString().substr(0, 10)),
            deactivateDate: this.formatDate(new Date().toISOString().substr(0, 10)),
            creationDate: this.formatDate(new Date().toISOString().substr(0, 10)),
            date: new Date().toISOString().substr(0, 10),
            activatedTime: '',
            deactivatedTime: '',
            activated: null,
            deactivated: null,
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
    /**
     * loads the page for creating a new poll with the pollId 0, else with the pollId of the poll to copy.
     **/
    mounted() {
        this.pollId = this.$route.params.pollCopy
        this.getCopyPoll()
        this.getTitle()
        this.getAnonType()
        this.getActDate()
        this.getDeactDate()
    },
    computed: {
        ...mapGetters({
            isAuthenticated: 'login/isAuthenticated',
            getUsername: 'login/getUsername',
            getPoll: 'PollCreation/getPoll',
        }),
    },
    methods: {
        validate() {
            this.$refs.form.validate()
        },
        /**
         * Puts all information in an object and directly access axios over above declared instance at PostMapping
         * createPoll in PollController. Depending on the pollID creates a new poll or copies the poll and then the
         * categories.
         **/
        async sendData() {
            this.activated = this.activateDate.concat('&', this.activatedTime)
            this.deactivated = this.deactivateDate.concat('&', this.deactivatedTime)
            const obj = {
                pollCreator: this.getUsername,
                anonymityStatus: this.selectedAnonymityType,
                pollName: this.title,
                pollCreatedAt: this.creationDate,
                activatedAt: this.activated,
                deactivatedAt: this.deactivated,
                pollStatus: 0,
                categoryChange: this.categoryChange,
                visibility: this.visibility,
                backgroundColor: this.backgroundColor,
                fontColor: this.fontColor,
                logo: this.logo,
                activated: this.switch1,
                deactivated: this.switch2,
            }
            console.log('obj: ', obj)
            if (this.pollId !== '0') {
                await this.$axios
                    .post('/createcopypoll', obj)
                    .catch()
                    .then((result) => {
                        this.newPollId = result.data
                    })
                await this.$axios.post('/copycategories/' + this.poll.pollId + '/' + this.newPollId).catch()
            } else {
                await this.$axios
                    .post('/createpoll', obj)
                    .catch()
                    .then((result) => {
                        this.newPollId = result.data
                    })
            }
            this.$router.push('/polls')
        },
        async goHome() {
            await this.sendData()
            await this.$router.push('/polls')
        },
        async goToNewPoll() {
            await this.sendData()
            await this.$router.push('/polls/' + this.newPollId)
        },
        formatDate(date) {
            console.log('formatDate')
            if (!date) return null
            const [year, month, day] = date.split('-')
            return `${day}.${month}.${year}`
        },
        parseDate(date) {
            console.log('parseDate')
            if (!date) return null
            const [day, month, year] = date.split('.')
            return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`
        },
        unparseDate(date) {
            console.log('unparseDate')
            if (!date) return new Date().toISOString.substr(0, 10)
            const [year, month, day] = date.split('-')
            return `${day.padStart(2, '0')}.${month.padStart(2, '0')}.${year}`
        },
        getActivationDate(date) {
            console.log('getActivationDate')
            if (date === null) return new Date().toISOString.substr(0, 10)
            const [day, month, year] = date.split('.')
            return `${year}-${month}-${day}`
        },
        reformatActivateDate() {
            console.log('reformatActivateDate')
            this.activateDate = this.formatDate(this.date)
        },
        reformatDeactivateDate() {
            console.log('reformatDeActivateDate')
            this.deactivateDate = this.formatDate(this.date)
        },
        parseTime(date) {
            console.log('parseTime')
            if (!date) return null
            return date.getHours() + ':' + date.getMinutes()
        },
        closeMenuAndUpdateA() {
            console.log('closeMenuAndUpdateA')
            this.menu = false
            this.reformatActivateDate()
        },
        closeMenuAndUpdateD() {
            console.log('closeMenuAndUpdateD')
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
        async getCopyPoll() {
            if (this.pollId !== '0') {
                await this.$store.dispatch('PollCreation/getCopyPoll', this.pollId)
                this.poll = this.getPoll
                this.categoryList = this.poll.categoryList
                console.log('poll', this.poll)
            }
        },
        async getTitle() {
            if (this.pollId !== '0') {
                const response = await this.$store.dispatch('PollCreation/getPollName', this.pollId)
                console.log(response)
                this.title = response
                this.$router.forceUpdate()
                return response
            }
            return 'Titel'
        },
        async getAnonType() {
            if (this.pollId !== '0') {
                const response = await this.$store.dispatch('PollCreation/getAnonType', this.pollId)
                console.log(response)
                if (response === 'Anonym') this.selectedAnonymityType = 1
                if (response === 'Teilanonym') this.selectedAnonymityType = 2
                if (response === 'Nichtanonym') this.selectedAnonymityType = 3
            }
        },
        async getActDate() {
            if (this.pollId !== '0') {
                const response = await this.$store.dispatch('PollCreation/getActDate', this.pollId)
                console.log(response)
                this.activateDate = response.substring(0, response.indexOf('&'))
                console.log('activatedDate', this.activateDate)
                this.activatedTime = response.substring(response.indexOf('&') + 1)
                console.log('activatedDate', this.activatedTime)
                return response
            } else {
                const today = new Date()
                this.activatedTime = today.getHours() + ':' + today.getMinutes()
            }
        },
        async getDeactDate() {
            if (this.pollId !== '0') {
                const response = await this.$store.dispatch('PollCreation/getDeactDate', this.pollId)
                console.log(response)
                this.deactivateDate = response.substring(0, response.indexOf('&'))
                console.log('deactivatedDate', this.deactivateDate)
                this.deactivatedTime = response.substring(response.indexOf('&') + 1)
                console.log('deactivatedDate', this.deactivatedTime)
                return response
            } else {
                const today = new Date()
                this.deactivatedTime = today.getHours() + ':' + today.getMinutes()
            }
        },
        saveTime(e, type) {
            if (type === 'd') {
                this.deactivatedTime = e
            } else if (type === 'a') {
                this.activatedTime = e
            }
        },
    },
}
</script>

<style scoped></style>
