<template>
    <div>
        <div>
            <v-card>
                <v-card-title>Teilnehmer zur Umfrage einladen</v-card-title>
                <v-col cols="6">
                    CSV Datei mit E-Mail Adressen hochladen:
                    <v-row>
                        <v-file-input
                            v-model="file"
                            class="ml-4 mr-3"
                            prepend-icon="mdi-file"
                            show-size
                            accept=".csv"
                        />
                        <v-btn color="primary" dark class="mb-2" @click="submitFile()">CSV hochladen</v-btn>
                    </v-row>
                </v-col>
                <v-col sm="6">
                    <v-card-actions>
                        <v-text-field
                            v-model="mail"
                            id="itemForm"
                            hint="E-Mail Adresse des Teilnehmers"
                            label="E-Mail Adresse eingeben"
                            @keypress.enter="addEmail"
                        ></v-text-field>
                        <v-col>
                            <v-btn color="primary" dark class="mb-2" @click="addEmail">Hinzufügen</v-btn>
                        </v-col>
                    </v-card-actions>
                    <v-card>
                        <v-list>
                            <v-subheader>
                                E-MAIL ADRESSEN
                                <v-spacer />
                                <v-btn color="accent" @click="items = []">Alle entfernen</v-btn>
                            </v-subheader>

                            <ul>
                                <li v-for="(item, index) in items" :key="item.id">
                                    <v-btn icon color="grey" @click="deleteEmail(index)">X</v-btn>
                                    {{ item }}
                                </li>
                            </ul>
                        </v-list>
                    </v-card>
                </v-col>
                <v-container fluid>
                    <v-row>
                        <v-col cols="12" md="6">
                            <v-sheet elevation="12" class="pa-12">
                                <v-textarea
                                    v-model="mailSubject"
                                    :auto-grow="autoGrow"
                                    :clearable="clearable"
                                    :counter="counter ? counter : false"
                                    :filled="filled"
                                    :flat="flat"
                                    :label="labelSubject"
                                    :loading="loading"
                                    :no-resize="noResize"
                                    :outlined="outlined"
                                    :placeholder="placeholderSubject"
                                    :rounded="rounded"
                                    :row-height="rowHeightSubject"
                                    :rows="rowsSubject"
                                    :shaped="shaped"
                                    :single-line="singleLine"
                                    :solo="solo"
                                ></v-textarea>
                                <v-textarea
                                    v-model="mailText"
                                    :auto-grow="autoGrow"
                                    :clearable="clearable"
                                    :counter="counter ? counter : false"
                                    :filled="true"
                                    :flat="flat"
                                    :hint="hint"
                                    :label="label"
                                    :loading="loading"
                                    :no-resize="noResize"
                                    :outlined="outlined"
                                    :persistent-hint="persistentHint"
                                    :placeholder="placeholder"
                                    :rounded="rounded"
                                    :row-height="rowHeight"
                                    :rows="rows"
                                    :shaped="shaped"
                                    :single-line="singleLine"
                                    :solo="solo"
                                ></v-textarea>
                                <v-row justify="end">
                                    <v-btn color="primary" dark class="mb-2" justify="end" @click="sendEmail"
                                        >E-Mail senden</v-btn
                                    >
                                </v-row>
                            </v-sheet>
                        </v-col>
                    </v-row>
                </v-container>
            </v-card>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            file: null,
            items: [],
            message: '',
            poll: [],
            mail: '',
            autoGrow: true,
            autofocus: true,
            clearable: true,
            counter: 0,
            filled: true,
            flat: false,
            hint: 'Um den Teilnahmelink hinzuzufügen, fügen SIe im Text "{link}" ein.',
            label: 'E-Mail Nachricht',
            loading: false,
            mailText:
                'Hallo Teilnehmer!\n\nHier ist dein persönlicher Teilnahmelink:\n' +
                '{link}\nViel Spaß bei der Teilnahme!\n\n' +
                'Mit freundlichen Grüßen\n' +
                'Dein Umfrato Team',
            noResize: false,
            outlined: false,
            persistentHint: true,
            placeholder: 'Geben Sie hier Ihre E-Mail Nachricht ein, die an den Teilnehmer gesendet werden soll.',
            rounded: false,
            rowHeight: 24,
            rows: 10,
            shaped: false,
            singleLine: false,
            solo: false,
            labelSubject: 'E-Mail Betreff',
            mailSubject: 'Einladung zur Umfrage',
            placeholderSubject: 'Geben Sie hier den E-Mail Betreff an.',
            rowsSubject: '1',
            rowHeightSubject: '1',
        }
    },
    created() {
        this.loadPoll()
    },
    copmuted: {
        pollId() {
            return parseInt(this.$route.params.MailSending)
        },
    },
    methods: {
        async loadPoll() {
            await this.$axios
                .get('/getonepoll', {
                    params: {
                        pollId: this.$route.params.MailSending,
                    },
                })
                .then((response) => {
                    this.poll = response.data
                    // console.log(this.poll)
                })
                .catch((error) => {
                    console.log(error)
                })
        },
        submitFile() {
            if (this.file) {
                const papa = require('papaparse')
                papa.parse(this.file, {
                    complete: (results) => {
                        for (let i = 0; i < results.data.length; i++) {
                            for (let j = 0; j < results.data[i].length; j++) {
                                if (results.data[i][j].includes('@')) {
                                    this.items.push(results.data[i][j])
                                }
                            }
                        }
                    },
                })
            }
        },
        sendEmail() {
            // console.log('Email Sending test')
            // console.log(this.items)
            const obj = {
                mailList: this.items,
                emailSubject: this.mailSubject,
                emailMessage: this.mailText,
                pollId: this.poll.pollId,
                anonymityStatus: this.poll.anonymityStatus,
            }
            this.$axios
                .post('/sendEmail', obj)
                .then(function () {
                    // console.log('Email sending succedeed.')
                })
                .catch((error) => console.log(error))
            this.$router.push('/polls')
        },
        addEmail() {
            const input = document.getElementById('itemForm')
            if (input.value !== '' && input.value.includes('@') && input.value.includes('.')) {
                this.items.push(input.value)
                // console.log(input.value)
                this.mail = ''
            }
        },
        deleteEmail(index) {
            this.items.splice(index, 1)
        },
    },
}
</script>

<style scoped></style>
