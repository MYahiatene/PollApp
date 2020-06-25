<template>
    <div>
        <v-container>
            <v-card class="mx-auto" max-width="1000" tile>
                <v-card-title> Willkommen, {{ getUsername }}! </v-card-title>
                <v-container>Name: {{ account.firstName }} {{ account.lastName }}</v-container>
                <v-container>E-Mail: {{ account.email }}</v-container>
                <v-container>Rolle: {{ account.role }}</v-container>
            </v-card>
        </v-container>
        <v-dialog v-model="dialog">
            <template v-slot:activator="{ on }">
                <v-btn color="primary" class="mb-2" v-on="on">Passwort ändern</v-btn>
            </template>
            <v-card>
                <v-card-title>
                    This is a dialog!
                </v-card-title>
            </v-card>
        </v-dialog>
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
        getUserVorNachname() {
            for (let i = 0; i < this.users.length; i++) {
                if ((this.users[i].username = this.getUsername)) {
                    return this.users[i].firstName + this.users[i].lastName
                } else {
                    return 'Something went wrong here!'
                }
            }
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
