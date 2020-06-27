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
                                            :type="showPassword1 ? 'text' : 'password'"
                                            :append-icon="showPassword1 ? 'mdi-eye' : 'mdi-eye-off'"
                                            @click:append="showPassword1 = !showPassword1"
                                            required
                                        ></v-text-field>
                                    </v-col>
                                    <v-col cols="12">
                                        <v-text-field
                                            v-model="newPassword2"
                                            label="Neues Passwort wiederholen"
                                            :type="showPassword2 ? 'text' : 'password'"
                                            :append-icon="showPassword2 ? 'mdi-eye' : 'mdi-eye-off'"
                                            @click:append="showPassword2 = !showPassword2"
                                            required
                                        ></v-text-field>
                                    </v-col>
                                    <!--ToDo: Make red and cursive-->
                                    <div v-if="incorrectPw" class="red--text font-italic">
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
            showPassword1: false,
            showPassword2: false,
            newPassword1: '',
            newPassword2: '',
            incorrectPw: false,
            userObj: {
                username: '',
                password: '',
            },
            failStatus: -1,
        }
    },
    computed: {
        ...mapGetters({
            authenticate: 'administration/getToken',
            getUsername: 'login/getUsername',
            getFailStatus: 'account/getFailStatus',
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
            console.log('failStatus', this.failStatus)
            this.incorrectPw = false
            this.dialog = false
        },
        /**
         * When "Speichern" is clicked and the Passwords are the same, the password is saved and updated
         * and if not the dialog doesn't close and gives you an error message.
         */
        savePw() {
            if (this.newPassword1 === this.newPassword2) {
                this.incorrectPw = false
                this.dialog = false

                this.userObj.username = this.account.username
                this.userObj.password = this.newPassword1

                // this.$store.dispatch('account/changePassword', this.userObj)
                // this.failStatus = this.getFailStatus

                this.$axios.defaults.baseURL = 'http://localhost:8088/api/'
                this.$axios.defaults.headers.common.Authorization = 'Bearer ' + localStorage.getItem('user-token')
                const status = this.$axios.put('/changePassword', this.userObj)

                this.failStatus = status
                console.log('failStatus', this.failStatus)
            } else {
                this.incorrectPw = true
            }
        },
    },
}
</script>

<style scoped></style>
