<template>
    <div>
        <v-container>
            <v-card class="mx-auto" max-width="800" tile>
                <v-card-title class="justify-center"> Willkommen, {{ account.firstName }}! </v-card-title>
                <v-container class="ma-1">
                    <v-container>
                        <span class="font-weight-bold">
                            Name:
                        </span>
                        {{ account.firstName }} {{ account.lastName }}</v-container
                    >
                    <v-container>
                        <span class="font-weight-bold">
                            Username:
                        </span>
                        {{ account.username }}</v-container
                    >
                    <v-container>
                        <span class="font-weight-bold">
                            E-Mail:
                        </span>
                        {{ account.email }}</v-container
                    >
                    <v-container>
                        <span class="font-weight-bold">
                            Rolle:
                        </span>
                        {{ account.role }}</v-container
                    >
                    <!--TODO: make red and cursive!-->
                    <v-container>
                        <div v-if="incorrectPw">
                            <span
                                >Die eingegebenen Passwörter stimmen nicht überein. Das Passwort wurde nicht
                                geändert,</span
                            >
                        </div>
                    </v-container>
                </v-container>
                <v-dialog v-model="dialog" persistent max-width="600px">
                    <template v-slot:activator="{ on }">
                        <v-btn color="primary" class="ma-4 float-left" v-on="on">Passwort ändern</v-btn>
                    </template>
                    <v-card>
                        <v-card-title>
                            Passwort ändern
                        </v-card-title>
                        <v-card-text>
                            <v-container>
                                <v-row>
                                    <v-col cols="12">
                                        <v-text-field
                                            v-model="newPassword1"
                                            label="Neues Passwort"
                                            :type="showPassword ? 'text' : 'password'"
                                            :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                                            @click:append="showPassword = !showPassword"
                                            required
                                        ></v-text-field>
                                    </v-col>
                                    <v-col cols="12">
                                        <v-text-field
                                            v-model="newPassword2"
                                            label="Neues Passwort wiederholen"
                                            :type="showPassword ? 'text' : 'password'"
                                            :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                                            @click:append="showPassword = !showPassword"
                                            required
                                        ></v-text-field>
                                    </v-col>
                                    <!--ToDo: Make red and cursive-->
                                    <div v-if="incorrectPw">
                                        <span>Die Passwörter stimmen nicht überein!</span>
                                    </div>
                                </v-row>
                            </v-container>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn color="primary" text @click="closePw">Abbrechen</v-btn>
                            <v-btn color="primary" text @click="savePw">Speichern</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-card>
        </v-container>
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
    name: 'Account',
    data() {
        return {
            token: '',
            account: '',
            users: [],
            authenticated: false,
            dialog: false,
            showPassword: false,
            newPassword1: '',
            newPassword2: '',
            incorrectPw: false,
        }
    },
    computed: {
        ...mapGetters({
            authenticate: 'administration/getToken',
            getUsername: 'login/getUsername',
        }),
    },
    created() {
        this.createToken()
        this.loadUserData()
    },
    methods: {
        async createToken() {
            await this.$store.dispatch('administration/setToken')
            this.token = this.authenticate
            const instance = this.$axios.create({
                baseURL: 'http://127.0.0.1:8088/api',
                headers: {
                    Authorization: 'Bearer ' + this.token,
                },
            })
            instance.get('/checkToken').then(
                (response) => {
                    if (response.status === 200) {
                        this.authenticated = true
                    }
                },
                (error) => {
                    console.log(error)
                }
            )
            return this.token
        },
        async loadUserData() {
            this.token = this.authenticate
            const instance = this.$axios.create({
                baseURL: 'http://127.0.0.1:8088/api',
                headers: {
                    Authorization: 'Bearer ' + this.token,
                },
            })
            await instance.get('/users').then((response) => {
                this.users = response.data
                this.users.forEach((ele) => {
                    ele.role = ele.authorities[0].authority
                })
            })
            this.getAccount()
        },
        getAccount() {
            for (let i = 0; i < this.users.length; i++) {
                if ((this.users[i].username = this.getUsername)) {
                    this.account = this.users[i]
                    console.log(this.account)
                }
            }
        },
        /**
         * When "Abbrechen" is clicked nothing happens, the changes will be discarded and the dialog will close.
         */
        closePw() {
            this.incorrectPw = false
            this.dialog = false
        },
        savePw() {
            if (this.newPassword1 === this.newPassword2) {
                this.incorrectPw = false
                this.dialog = false
                console.log('Das muss jetzt noch gespeichert werden')
            } else {
                this.incorrectPw = true
            }
            console.log('PW1', this.newPassword1)
            console.log('PW2', this.newPassword2)
        },
    },
}
</script>

<style scoped></style>
