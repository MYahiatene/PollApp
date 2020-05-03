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

                    <v-row no-gutters>
                        <v-switch
                            v-model="switch3"
                            label="Vor- und Zurückspringen zwischen Kategorien erlaubt"
                        ></v-switch>
                    </v-row>

                    <v-row no-gutters>
                        <v-switch
                            v-model="switch4"
                            :value="false"
                            label="Sichtbarkeit der Gesamtanzahl der Fragen"
                            @change="changeVisibility(value)"
                        >
                        </v-switch>
                    </v-row>
                </v-container>

                <br />

                <v-btn color="primary" nuxt to="/QuestionOverview">
                    Erstellen
                </v-btn>
            </v-form>
        </v-container>
    </v-card>
</template>

<script>
import { mapMutations } from 'vuex'
import DateTimePicker from '../components/dateTimePicker'
export default {
    name: 'QuestionCreation',
    components: { DateTimePicker },
    data() {
        return {
            switch1: false,
            switch2: false,
            // flag to set the attribute of changing the sites as a participant
            switch3: false,
            // flag to set the visibility of the number of questions
            switch4: false,
            valid: false,
            title: '',
            anonymityTypes: ['Anonym', 'Teilanonym', 'Nicht anonym'],
            // rules that assert that the title attribute must be set and that the title length is below 10 characters (that part can be changed)
            titleRules: [(v) => !!v || 'Titel fehlt', (v) => v.length <= 10 || 'Name must be less than 10 characters'],
            // rules that assert that the anonymity attribute must be set.
            anonymityRules: [(v) => !!v || 'Anonymitätsgrad fehlt.'],
        }
    },
    methods: {
        ...mapMutations(['changeVisible']),
        // validates the form (so the button is only enabled when the inputs are correct
        validate() {
            this.$refs.form.validate()
        },
        // changes the visibility of the number of questions depending on the switch button
        changeVisibility(value) {
            this.changeVisible(value)
        },
    },
}
</script>

<style scoped></style>
