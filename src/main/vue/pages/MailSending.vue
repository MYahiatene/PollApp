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
                                    {{ item }}
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
export default {
    data() {
        return {
            file: null,
            items: [],
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
                    // console.log(results.data[0][1])
                    for (let i = 0; i < results.data.length; i++) {
                        for (let j = 0; j < results.data[i].length; j++) {
                            this.items.push(results.data[i][j])
                        }
                    }
                    this.$axios.post('/sendCsv', this.items).catch()
                    // console.log(this.items)
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
            if (input.value !== '') {
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
