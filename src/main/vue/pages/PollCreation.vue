<template>
    <!--    This page includes all the necessary controls to create a new Poll-->
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
                                v-model="poll.pollname"
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
                                v-model="poll.anonymityStatus"
                                :items="anonymityTypes"
                                placeholder="Anonymitätsgrad"
                                :rules="anonymityRules"
                            ></v-overflow-btn>
                        </v-col>
                    </v-row>
                    <v-row no-gutters>
                        <v-switch v-model="switch1" label="lege Aktivierungsdatum fest"> </v-switch>
                    </v-row>

                    <v-row v-if="switch1" no-gutters>
                        <v-col cols="12" md="4">
                            <date-time-picker v-model="poll.activatedAt" />
                        </v-col>
                    </v-row>

                    <v-row no-gutters>
                        <v-switch v-model="switch2" label="lege Deaktivierungsdatum fest"></v-switch>
                    </v-row>
                    <v-row v-if="switch2" no-gutters>
                        <v-col cols="12" md="4">
                            <date-time-picker v-model="poll.deactivatedAt" />
                        </v-col>
                    </v-row>

                    <v-row no-gutters>
                        <v-switch
                            v-model="switch3"
                            label="Vor- und Zurückspringen zwischen Kategorien erlaubt"
                        ></v-switch>
                    </v-row>

                    <v-row no-gutters>
                        <v-switch
                            v-model="switch4"
                            label="Sichtbarkeit der Gesamtanzahl der Fragen"
                            @change="changeVisibility()"
                        >
                        </v-switch>
                    </v-row>

                    <v-row no-gutters>
                        <v-switch v-model="switch5" label="eigenes Design für die Umfrage"></v-switch>
                    </v-row>
                    <v-row v-if="switch5" no-gutters>
                        <v-col cols="12" md="4">
                            Hintergrundfarbe
                            <v-color-picker @change="backgroundColor" />
                        </v-col>
                        <v-col cols="12" md="4">
                            Schriftfarbe
                            <v-color-picker @change="fontColor" />
                        </v-col>
                        <v-col cols="12" md="4">
                            Logo hochladen
                            <input type="file" @change="showImage" />
                            <img :src="poll.logo" alt="Image" />
                        </v-col>
                    </v-row>
                </v-container>

                <br />

                <v-btn color="primary" nuxt to="/PollEditing" @click="savePoll">
                    Erstellen
                </v-btn>
            </v-form>
        </v-container>
    </v-card>
</template>

<script>
import { mapMutations, mapActions } from 'vuex'
import DateTimePicker from '../components/dateTimePicker'

export default {
    name: 'QuestionCreation',
    components: { DateTimePicker },
    data() {
        return {
            poll: {
                pollname: '',
                anonymityStatus: null,
                activatedAt: null,
                deactivatedAt: null,
                logo: null,
                hexaBackground: null,
                hexaFont: null,
                // flag-settings for visbility of numbers of questions and allowing back/forth should also be added
            },
            switch1: false,
            switch2: false,
            // flag to set the attribute of changing the sites as a participant
            switch3: false,
            // flag to set the visibility of the number of questions
            switch4: false,
            switch5: false,
            valid: false,
            anonymityTypes: ['Anonym', 'Teilanonym', 'Nicht anonym'],
            // rules that assert that the title attribute must be set and that the title length is below 10 characters (that part can be changed)
            titleRules: [(v) => !!v || 'Titel fehlt', (v) => v.length <= 20 || 'Name must be less than 20 characters'],
            // rules that assert that the anonymity attribute must be set.
            anonymityRules: [(v) => !!v || 'Anonymitätsgrad fehlt.'],
        }
    },
    methods: {
        ...mapMutations({
            changeVisibility: 'questionCreation/changeVisible',
        }),
        ...mapActions(['savePoll']),
        // validates the form (so the button is only enabled when the inputs are correct)
        validate() {
            this.$refs.form.validate()
        },
        // Shows the uploded logo for verification
        showImage(e) {
            const image = e.target.files[0]
            const reader = new FileReader()
            reader.readAsDataURL(image)
            reader.onload = (e) => {
                this.poll.logo = e.target.result
            }
        },
        // creates and saves the poll in the database
        createPoll() {
            // this.$store.dispatch('PollCreation/createPoll')
        },
        ...mapActions({ createPoll: 'PollCreation/createPoll' }),
        backgroundColor(e) {
            this.hexaBackground = e.payload[0].hexa
        },
        fontColor(e) {
            this.hexaFont = e.payload[0].hexa
        },
    },
}
</script>

<style scoped></style>
