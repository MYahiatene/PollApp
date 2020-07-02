<template>
    <div>
        <div>
            <v-container class="grey lighten-5">
                <v-card-title>Teilnehmer zur Umfrage einladen</v-card-title>
                <v-col>
                    CSV Datei mit E-Mail Adressen hochladen:
                    <input id="file" ref="file" type="file" @change="handleFileUpload()" />
                    <v-btn @click="submitFile()">CSV hochladen</v-btn>
                </v-col>
                <v-col>
                    <v-card-actions>
                        <input id="itemForm" placeholder="E-Mail Adresse" @keypress.enter="addEmail" />
                        <v-btn @click="addEmail">Hinzufügen</v-btn>
                    </v-card-actions>
                </v-col>
                <v-col>
                    <v-card>
                        <v-list>
                            <v-subheader>E-MAIL ADRESSEN</v-subheader>
                            <ul>
                                <li v-for="(item, index) in items" :key="item.id">
                                    <v-btn x-small color="grey" @click="deleteEmail(index)">X</v-btn>
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
                                    :hint="hint"
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
                                    :filled="filled"
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
                                    <v-btn justify="end" @click="sendEmail">E-Mail senden</v-btn>
                                </v-row>
                            </v-sheet>
                        </v-col>
                    </v-row>
                </v-container>
            </v-container>
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
                'Dein Umrato Team',
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
    mounted() {
        // this.initializeMailList()
    },
    methods: {
        async initializeMailList() {
            await this.$axios.get('/emailList').then((response) => {
                this.emailAdresses = response.data
                console.log(response.data)
            })
        },
        submitFile() {
            // const reader = new FileReader()
            this.$axios.defaults.baseURL = 'http://127.0.0.1:8088/api'
            this.$axios.defaults.headers.common = {
                Authorization:
                    'Bearer ' +
                    'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZWN1cmUtYXBpIiwiYXVkIjoic2VjdXJlLWFwcCIsInN1YiI6InRiZXR0bWFubiIsImV4cCI6MTU5NDMwMzI5MCwicm9sIjpbIkFkbWluIl19._83mp73eHuu5JP8gtDIC3Jho5V7_pA-R-2pLOuRKfLe_NPtO5VtPjn3jBtBCfqJI_aaRjquFgeA5s7Y8JRAxvQ',
            }
            console.log('submitFile test')
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
                    this.$axios.post('/sendCsv', this.items).catch()
                },
            })
        },
        sendEmail() {
            this.$axios.defaults.baseURL = 'http://127.0.0.1:8088/api'
            this.$axios.defaults.headers.common = {
                Authorization:
                    'Bearer ' +
                    'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZWN1cmUtYXBpIiwiYXVkIjoic2VjdXJlLWFwcCIsInN1YiI6InRiZXR0bWFubiIsImV4cCI6MTU5NDMwMzI5MCwicm9sIjpbIkFkbWluIl19._83mp73eHuu5JP8gtDIC3Jho5V7_pA-R-2pLOuRKfLe_NPtO5VtPjn3jBtBCfqJI_aaRjquFgeA5s7Y8JRAxvQ',
            }
            console.log('Email Sending test')
            console.log(this.items)
            const obj = {
                mailList: this.items,
                emailSubject: this.mailSubject,
                emailMessage: this.mailText,
            }
            this.$axios
                .post('/sendEmail', obj)
                .then(function () {
                    console.log('Email sending succedeed.')
                })
                .catch((error) => console.log(error))
        },
        handleFileUpload() {
            this.file = this.$refs.file.files[0]
        },
        addEmail() {
            const input = document.getElementById('itemForm')
            if (input.value !== '' && input.value.includes('@') && input.value.includes('.')) {
                this.items.push(input.value)
                console.log(input.value)
                input.value = ''
            }
        },
        deleteEmail(index) {
            this.items.splice(index, 1)
        },
    },
}
</script>

<style scoped></style>
