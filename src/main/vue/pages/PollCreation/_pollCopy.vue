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
                                label="Umfragentitel"
                                :rules="titleRules"
                                required
                                clearable
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

                    <!--Serial surveys-->
                    <v-row no-gutters>
                        <v-switch v-model="switch0" label="Als Serienumfrage erstellen"></v-switch>
                    </v-row>
                    <v-row v-if="switch0" no-gutters>
                        <v-col cols="12" lg="6">
                            <v-card class="mt-5">
                                <v-card-title>Dynmischer Name</v-card-title>
                                <v-card-text
                                    >Klicken sie eines dieser Attribute an, um es bei der Erstellung der Umfrage im
                                    Titel einfügen zu lassen
                                </v-card-text>
                                <v-card-actions>
                                    <v-chip class="ma-1" @click="addNumber">Nummer</v-chip>
                                    <v-chip class="ma-1" @click="addDay">Tag</v-chip>
                                    <v-chip class="ma-1" @click="addMonth">Monat</v-chip>
                                    <v-chip class="ma-1" @click="addYear">Jahr</v-chip>
                                    <v-chip class="ma-1" @click="addCalendarWeek">Kalenderwoche</v-chip>
                                    <v-chip class="ma-1" @click="addDayOfWeek">Wochentag</v-chip>
                                </v-card-actions>
                            </v-card>
                            <v-card class="mt-3 mb-3">
                                <v-card-title>Dynamische Bereitstellung</v-card-title>
                                <v-card-text>
                                    Stellen Sie hier ein, in welchem Rythmus die Umfrage bereitgestellt werden soll
                                </v-card-text>
                                <v-card-actions>
                                    <v-col>
                                        <v-row no-gutters>
                                            <v-text-field
                                                v-model="rhythm"
                                                class="mr-5 ml-3"
                                                label="Rythmus"
                                                prefix="Alle"
                                                hint="z.B. Alle 2 Wochen"
                                                type="Number"
                                                min="1"
                                                :required="switch0"
                                                :rules="rhythmRules"
                                            />
                                            <v-overflow-btn
                                                class="mr-3 ml-5"
                                                v-model="selectedLevelAsText"
                                                dense
                                                label="Zeiteinheit"
                                                :required="switch0"
                                                :items="levels"
                                                @change="(value) => switchLevel(value)"
                                            />
                                        </v-row>
                                        <v-label v-if="selectedLevel > 0" x-small>
                                            Mehrere Werte können getrennt eingegeben werden (z.B. "1 5" für Mittwoch und
                                            Freitag)
                                        </v-label>
                                        <v-row>
                                            <v-text-field
                                                v-if="selectedLevel > 2"
                                                v-model="month"
                                                class="mr-5 ml-5"
                                                label="Monat im Jahr"
                                                prefix=""
                                                hint="z.B. Monat 3"
                                                min="1"
                                                max="12"
                                                :rules="monthRules"
                                            />
                                            <v-text-field
                                                v-if="selectedLevel > 1"
                                                v-model="week"
                                                class="mr-5 ml-5"
                                                :label="
                                                    selectedLevel > 2 && stringToList(month).length > 0 <= 0
                                                        ? 'Kalenderwoche'
                                                        : 'Woche im Monat'
                                                "
                                                :hint="
                                                    selectedLevel > 2
                                                        ? month <= 0
                                                            ? 'Für Monatsbezug Monat festlegen'
                                                            : 'Für Jahresbezug Monat frei lassen'
                                                        : 'Woche im Monat'
                                                "
                                                min="1"
                                                :max="selectedLevel > 2 && month <= 0 ? 53 : 4"
                                                :rules="weekRules"
                                            />
                                            <v-text-field
                                                v-if="selectedLevel > 0"
                                                v-model="day"
                                                class="mr-5 ml-5"
                                                :label="dayLabel"
                                                :hint="dayHint"
                                                min="1"
                                                max="365"
                                                :required="switch0"
                                                :rules="dayRules"
                                            />
                                        </v-row>
                                    </v-col>
                                </v-card-actions>
                            </v-card>
                            <v-card class="mb-5">
                                <v-card-title>Beenden</v-card-title>
                                <v-card-text>Wann soll die Umfrage wieder beendet werden? </v-card-text>
                                <v-col>
                                    <v-row>
                                        <v-overflow-btn
                                            v-model="selectedDeactivation"
                                            label="Deaktivierungsgrund"
                                            :items="deactivationReasons"
                                            :required="switch0"
                                        />
                                    </v-row>
                                    <v-row v-if="selectedDeactivation === 'Datum'">
                                        <v-menu
                                            ref="menu"
                                            v-model="menuF"
                                            :close-on-content-click="false"
                                            transition="scale-transition"
                                            offset-y
                                            max-width="290px"
                                            min-width="290px"
                                        >
                                            <template v-slot:activator="{ on }">
                                                <v-text-field
                                                    v-model="finishingString"
                                                    label="Umfrage läuft bis einschließlich"
                                                    @blur="finishingDate = parseDate(finishingString)"
                                                    :required="selectedDeactivation === 'Datum'"
                                                    v-on="on"
                                                />
                                            </template>
                                            <v-date-picker
                                                v-model="finishingDate"
                                                no-title
                                                show-current="true"
                                                locale="de"
                                                :min="new Date().toISOString().substr(0, 10)"
                                                max="2100-01-01"
                                                @input="finishingString = unparseDate(finishingDate)"
                                            />
                                        </v-menu>
                                    </v-row>
                                    <v-row v-else-if="selectedDeactivation.length > 0">
                                        <v-text-field
                                            v-model="finishingNumber"
                                            class="mr-5 ml-5"
                                            :label="
                                                'Deaktivieren sobald ' +
                                                selectedDeactivation +
                                                (selectedDeactivation === 'Teilnehmerzahl' ? ' unter' : '')
                                            "
                                            :required="selectedDeactivation !== 'Datum'"
                                            :suffix="selectedDeactivation === 'Umfragenanzahl' ? 'erreicht' : ''"
                                            type="Number"
                                            min="1"
                                        />
                                    </v-row>
                                </v-col>
                            </v-card>
                        </v-col>
                    </v-row>
                    <v-row no-gutters>
                        <v-switch
                            v-model="switch1"
                            :disabled="switch0"
                            :label="switch0 ? 'lege allgemeines Aktivierungsdatum fest' : 'lege Aktivierungsdatum fest'"
                        ></v-switch>
                    </v-row>
                    <v-row v-if="switch1 || switch0" no-gutters>
                        <v-col cols="12" lg="2">
                            <v-menu
                                ref="menu"
                                v-model="menuA"
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
                                        :required="switch0"
                                        @blur="date = parseDate(activateDate)"
                                        v-on="on"
                                    />
                                </template>
                                <v-date-picker
                                    v-model="date"
                                    no-title
                                    show-current="true"
                                    locale="de"
                                    :min="new Date().toISOString().substr(0, 10)"
                                    max="2100-01-01"
                                    @input="closeMenuAndUpdateA"
                                />
                            </v-menu>
                        </v-col>
                        <v-col>
                            <v-text-field
                                v-model="activatedTime"
                                label="Uhrzeit"
                                type="time"
                                style="width: 170px;"
                                @change="saveTime($event, 'a')"
                            />
                        </v-col>
                    </v-row>

                    <v-row no-gutters>
                        <v-switch
                            v-model="switch2"
                            :disabled="switch0"
                            :label="
                                switch0 ? 'lege allgemeines Deaktivierungsdatum fest' : 'lege Deaktivierungsdatum fest'
                            "
                        ></v-switch>
                    </v-row>
                    <v-row v-if="switch2 || switch0" no-gutters>
                        <v-col cols="12" md="2">
                            <v-menu
                                ref="menu"
                                v-model="menuD"
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
                                        :required="switch0"
                                        @blur="date = parseDate(deactivateDate)"
                                        v-on="on"
                                    />
                                </template>
                                <v-date-picker
                                    v-model="date"
                                    no-title
                                    show-current="true"
                                    locale="de"
                                    :min="getActivationDate(activateDate)"
                                    max="2100-01-01"
                                    @input="closeMenuAndUpdateD"
                                />
                            </v-menu>
                        </v-col>
                        <v-col
                            ><v-text-field
                                v-model="deactivatedTime"
                                label="Uhrzeit"
                                type="time"
                                style="width: 170px;"
                                @change="saveTime($event, 'd')"
                        /></v-col>
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
                <v-btn :disabled="!valid || !serialPollValid" color="success" class="mr-4" @click="goHome()"
                    >Erstellen</v-btn
                >
                <v-btn :disabled="!valid || !serialPollValid" color="success" class="mr-4" @click="goToNewPoll()"
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
            switch0: true,
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
            newPollId: -1,
            activateDate: this.formatDate(new Date().toISOString().substr(0, 10)),
            deactivateDate: this.formatDate(new Date().toISOString().substr(0, 10)),
            creationDate: this.formatDate(new Date().toISOString().substr(0, 10)),
            date: new Date().toISOString().substr(0, 10),
            activatedTime: '',
            deactivatedTime: '',
            activated: null,
            deactivated: null,
            menuA: false,
            menuD: false,
            menuF: false,
            selectedAnonymityType: '',
            levels: ['Tage', 'Wochen', 'Monate', 'Jahre'],
            selectedLevel: -1,
            selectedLevelAsText: '',
            deactivationReasons: ['Datum', 'Umfragenanzahl', 'Teilnehmerzahl'],
            selectedDeactivation: '',
            finishingDate: '',
            finishingString: '',
            finishingNumber: null,
            rhythm: '',
            day: '',
            week: '',
            month: '',
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
            rhythmRules: [
                (v) => !!v || 'Rythmus ist nicht angegeben',
                (v) => Number(v) % 1 === 0 || 'Rythmus muss ganze Zahl sein',
            ],
            dayRules: [(v) => this.dayValid(v)],
            weekRules: [(v) => this.weekValid(v)],
            monthRules: [(v) => this.monthValid(v)],
        }
    },
    /**
     * loads the page for creating a new poll with the pollId 0, else with the pollId of the poll to copy.
     **/
    computed: {
        ...mapGetters({
            isAuthenticated: 'login/isAuthenticated',
            getUsername: 'login/getUsername',
            getPoll: 'PollCreation/getPoll',
        }),
        dayLabel() {
            switch (this.selectedLevel) {
                case 1:
                    return 'Tag in der Woche'
                case 2:
                    if (this.week <= 0) {
                        return 'Tag im Monat'
                    } else {
                        return 'Tag in der Woche'
                    }
                case 3:
                    if (this.week <= 0) {
                        if (this.month <= 0) {
                            return 'Tag im Jahr'
                        } else {
                            return 'Tag im Monat'
                        }
                    } else {
                        return 'Tag in der Woche'
                    }
            }
            return ''
        },
        dayHint() {
            switch (this.selectedLevel) {
                case 1:
                    return 'Tage in der Woche'
                case 2:
                    if (this.week <= 0) {
                        return 'Für Wochenbezug Woche festlegen'
                    } else {
                        return 'Für Monatsbezug Woche freilassen'
                    }
                case 3:
                    if (this.week <= 0) {
                        if (this.month <= 0) {
                            return 'Für Monatsbezug Monat festlegen'
                        } else {
                            return 'Für Wochenbezug Woche festlegen'
                        }
                    } else {
                        return 'Für Monatsbezug Woche freilassen'
                    }
            }
            return ''
        },
    },
    mounted() {
        this.pollId = this.$route.params.pollCopy
        this.getCopyPoll()
        this.getTitle()
        this.getAnonType()
        this.getActDate()
        this.getDeactDate()
    },
    methods: {
        validate() {
            this.$refs.form.validate()
        },
        stringToList(text) {
            const validChars = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'] // simpler than JS's ' ' == 0 etc.
            const list = []
            if (text) {
                const asArray = text.split('')
                let elem = ''
                for (let i = 0; i < asArray.length; i++) {
                    if (validChars.includes(asArray[i])) {
                        elem = elem + asArray[i]
                    } else {
                        if (!list.includes(Number(elem)) && elem.length > 0) {
                            list.push(Number(elem))
                        }
                        elem = ''
                    }
                }
                if (elem.length > 0) {
                    if (!list.includes(Number(elem))) {
                        list.push(Number(elem))
                    }
                }
            }
            list.sort((a, b) => {
                return a - b
            })
            console.log(list)
            return list
        },
        serialPollValid() {
            if (this.switch0) {
                if (
                    this.dayValid(this.day) &&
                    this.rhythm > 0 &&
                    this.selectedLevel > -1 &&
                    !!this.selectedDeactivation
                ) {
                    switch (this.selectedDeactivation) {
                        case 'Datum':
                            return this.finishingString.length > 0
                        case 'Umfragenanzahl':
                        case 'Teilnehmerzahl':
                            return this.finishingNumber > 0
                    }
                    return false
                }
                return false
            }
            return true
        },
        dayValid(dayString) {
            const list = this.stringToList(dayString)
            const monthContext = this.monthValid(this.month) === true && this.month.length > 0
            const weekContext = this.weekValid(this.week) === true && this.week.length > 0
            console.log('monthContext:', monthContext)
            console.log('weekContext:', weekContext)
            if (list.length > 0) {
                for (const elem of list) {
                    if (weekContext && (elem < 1 || elem > 7)) {
                        return 'Kein Wochentag: "' + elem + '"'
                    } else if (monthContext && (elem < 1 || elem > 31)) {
                        return 'Kein Tag im Monat: "' + elem + '"'
                    } else if (elem < 1 || elem > 365) {
                        return 'Kein Tag im Jahr: "' + elem + '"'
                    }
                }
                return true
            } else if (dayString.length > 0) {
                return 'Tagangabe ungültig'
            } else {
                return 'Keine Tagangabe vorhanden'
            }
        },
        weekValid(weekString) {
            const list = this.stringToList(weekString)
            const monthContext = this.monthValid(this.month) === true && this.month.length > 0
            console.log('monthContextForWeek:', monthContext)
            if (list.length > 0 || weekString.length === 0) {
                for (const elem of list) {
                    if (monthContext && (elem < 1 || elem > 5)) {
                        return 'Keine Woche im Monat: "' + elem + '"'
                    } else if (elem < 1 || elem > 53) {
                        return 'Keine Kalenderwoche: "' + elem + '"'
                    }
                }
                return true
            } else {
                return 'Wochenangabe ungültig'
            }
        },
        monthValid(monthString) {
            const list = this.stringToList(monthString)
            if (list.length > 0 || monthString.length === 0) {
                for (const elem of list) {
                    if (elem < 1 || elem > 12) {
                        return 'Kein Monat: "' + elem + '"'
                    }
                }
                return true
            } else {
                return 'Monatsangabe ungültig'
            }
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
                level: this.selectedLevel,
                repeat: this.rhythm,
                repeatUntil:
                    this.deactivationReasons.indexOf(this.selectedDeactivation) === 0
                        ? this.finishingDate
                        : this.finishingString,
                stoppingReason: this.deactivationReasons.indexOf(this.selectedDeactivation),
                day: this.stringToList(this.day),
                week: this.stringToList(this.week),
                month: this.stringToList(this.month),
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
        addNumber() {
            this.title += ':nr:'
        },
        addDay() {
            this.title += ':tg:'
        },
        addMonth() {
            this.title += ':mt:'
        },
        addYear() {
            this.title += ':jr:'
        },
        addCalendarWeek() {
            this.title += ':kw:'
        },
        addDayOfWeek() {
            this.title += ':wt:'
        },
        switchLevel(choice) {
            console.log(choice)
            switch (choice) {
                case 'Tage':
                    this.selectedLevel = 0
                    break
                case 'Wochen':
                    this.selectedLevel = 1
                    break
                case 'Monate':
                    this.selectedLevel = 2
                    break
                case 'Jahre':
                    this.selectedLevel = 3
                    break
            }
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
