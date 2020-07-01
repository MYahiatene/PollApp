<template>
    <div class="container">
        <div class="large-12 medium-12 small-12 cell">
            <label>
                <v-card class="mr-0" max-width="700" tile>
                    CSV Datei mit E-Mail Adressen hochladen:
                    <input id="file" ref="file" type="file" @change="handleFileUpload()" />
                </v-card>
                <v-btn @click="submitFile()">CSV hochladen</v-btn>
            </label>
        </div>
        <template>
            <div>
                <v-row>
                    <v-card class="mr-0" max-width="400" tile>
                        <input id="itemForm" placeholder="E-Mail Adresse" @keypress.enter="addEmail" />
                    </v-card>
                    <v-btn @click="addEmail">Add eMail</v-btn>
                </v-row>
            </div>
            <v-col align="start" justify="start">
                <v-row align="start" justify="start">
                    <v-card class="mr-0" max-width="400" tile>
                        <v-list
                            :disabled="disabled"
                            :dense="dense"
                            :two-line="twoLine"
                            :three-line="threeLine"
                            :shaped="shaped"
                            :flat="flat"
                            :subheader="subheader"
                            :sub-group="subGroup"
                            :nav="nav"
                            :avatar="avatar"
                            :rounded="rounded"
                        >
                            <v-subheader>E-MAIL ADRESSEN</v-subheader>
                            <ul>
                                <li v-for="(item, index) in items" :key="item.id">
                                    <v-btn x-small color="red" @click="deleteEmail(index)">X</v-btn>
                                    {{ item.text }}
                                </li>
                            </ul>
                        </v-list>
                    </v-card>
                </v-row>
            </v-col>
        </template>
        <template>
            <div>
                <v-row align="start" justify="end">
                    <v-card class="mr-10" max-width="800" tile>
                        <span>E-Mail Nachricht</span>
                        <br />
                        <textarea
                            v-model="message"
                            placeholder="Hallo Teilnehmer!
                     Hier ist Ihr persönlicher Teilnahmelink.
"
                        ></textarea>
                    </v-card>
                    <v-btn @click="sendEmail">E-Mail senden</v-btn>
                </v-row>
            </div>
        </template>
    </div>
</template>

<script>
const axios = require('axios')
const instance = axios.create({
    baseURL: 'http://127.0.0.1:8088/api/',
    timeout: 1000,
    // headers: { 'X-Custom-Header': 'foobar' },
    headers: { 'Content-Type': 'multipart/form-data' },
})
export default {
    data() {
        return {
            file: '',
            items: [{ text: 'eins' }, { text: 'zwei' }, { text: 'drei' }],
        }
    },
    mounted() {
        this.initializeMailList()
    },
    methods: {
        async initializeMailList() {
            await this.$axios.get('/emailList').then((response) => {
                this.emailAdresses = response.data
                console.log(response.data)
            })
        },
        submitFile() {
            console.log('submitFile test')
            const formData = new FormData()

            formData.append('file', this.file, 'mailcsv')
            console.log(formData)
            instance
                .post('/sendCsv', formData)
                .then(function () {
                    console.log('Success.')
                })
                .catch(function () {
                    console.log('Failed.')
                })
        },
        sendEmail() {
            console.log('Email Sending test')
            const obj = {
                mailList: this.items,
                csvFile: '',
            }
            instance.post('/sendEmail', obj).catch()
        },
        handleFileUpload() {
            this.file = this.$refs.file.files[0]
        },
        addEmail() {
            const input = document.getElementById('itemForm')

            if (input.value !== '') {
                this.items.push({
                    text: input.value,
                })
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
