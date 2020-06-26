<template>
    <div>
        <v-container>
            <v-card class="mx-auto" max-width="800" tile>
                <v-card-title class="justify-center"> Willkommen, {{ getUsername }}! </v-card-title>
                <v-container class="ma-1">
                    <v-container>
                        <span class="font-weight-bold">
                            Name:
                        </span>
                        {{ account.firstName }} {{ account.lastName }}</v-container
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
                                        <v-text-field label="Neues Passwort" type="password" required></v-text-field>
                                    </v-col>
                                    <v-col cols="12">
                                        <v-text-field
                                            label=" Neues Password wiederholen"
                                            type="password"
                                            required
                                        ></v-text-field>
                                    </v-col>
                                </v-row>
                            </v-container>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn color="primary" text @click="dialog = false">Abbrechen</v-btn>
                            <v-btn color="primary" text @click="dialog = false">Speichern</v-btn>
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
            search: '',
            expanded: [],
            singleExpand: false,
            sortBy: '',
            editedIndex: -1,
            editedUser: {
                username: '',
                role: '',
                email: '',
                firstName: '',
                lastName: '',
            },
            defaultUser: {
                username: '',
                role: '',
                email: '',
                firstName: '',
                lastName: '',
            },
            roles: ['Admin', 'Umfrageersteller', 'Umfragebearbeiter', 'Teilnehmer'],
            headers: [
                { text: 'Username', value: 'username', align: 'start' },
                {
                    text: 'Rolle',
                    value: 'role',
                },
                {
                    text: '',
                    value: 'actions',
                    sortable: false,
                },
                {
                    text: '',
                    value: 'data-table-expand',
                    sortable: false,
                    width: '1',
                },
            ],
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
    },
}
</script>

<style scoped></style>
