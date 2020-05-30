<template>
    <v-container v-if="authenticated === false">
        <v-card class="mx-auto" max-width="400" outlined>
            <v-list-item three-line>
                <v-list-item-content class="center">
                    <v-list-item-title class="headline mb-1">Zugriff verweigert</v-list-item-title>
                    <v-list-item-subtitle>Bitte loggen Sie sich ein</v-list-item-subtitle>
                </v-list-item-content>
            </v-list-item>

            <v-card-actions>
                <v-spacer></v-spacer>
                <div class="my-2">
                    <v-btn depressed color="secondary" to="/Login">Login</v-btn>
                </div>
            </v-card-actions>
        </v-card>
    </v-container>
    <v-container v-else>
        <v-card class="mx-auto" max-width="1000">
            <v-card-title>
                Nutzer
                <v-spacer></v-spacer>
                <v-text-field
                    v-model="search"
                    append-icon="mdi-magnify"
                    label="Search"
                    single-line
                    hide-details
                ></v-text-field>
                <v-spacer></v-spacer>
                <v-dialog v-model="dialog" max-width="700px">
                    <template v-slot:activator="{ on }">
                        <v-btn color="primary" dark class="mb-2" v-on="on">Neuen Nutzer anlegen</v-btn>
                    </template>
                    <v-card>
                        <v-card-title>
                            <span class="headline">{{ formTitle }}</span>
                        </v-card-title>

                        <v-card-text>
                            <v-container>
                                <v-row>
                                    <v-col cols="12" sm="6" md="4">
                                        <v-text-field v-model="editedUser.username" label="Username"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" sm="6" md="4">
                                        <v-text-field v-model="editedUser.firstName" label="Vorname"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" sm="6" md="4">
                                        <v-text-field v-model="editedUser.lastName" label="Nachname"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" sm="6" md="4">
                                        <v-text-field v-model="editedUser.email" label="E-Mail"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" sm="6" md="5">
                                        <v-select
                                            v-model="editedUser.role"
                                            :items="roles"
                                            hide-details
                                            label="Rolle zuweisen"
                                            single-line
                                        ></v-select>
                                    </v-col>
                                </v-row>
                            </v-container>
                        </v-card-text>

                        <v-card-actions>
                            <v-btn color="blue darken-1" text @click="close">Abbrechen</v-btn>
                            <v-spacer></v-spacer>
                            <v-btn color="blue darken-1" text @click="save">{{ formAction }}</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-card-title>
            <v-data-table
                :headers="headers"
                :items="users"
                :search="search"
                item-key="username"
                :single-expand="singleExpand"
                :expanded.sync="expanded"
                show-expand
            >
                <template v-slot:item.actions="{ item }">
                    <v-icon small class="mr-2" @click="editUser(item)">
                        mdi-pencil
                    </v-icon>
                    <v-icon small @click="deleteUser(item)">
                        mdi-delete
                    </v-icon>
                </template>
                <template v-slot:item.avatar="{ item }">
                    <div class="p-2">
                        <v-avatar v-if="item.avatar !== 'default'">
                            <v-img :src="item.avatar" :alt="item.username" height="50px" width="50px"></v-img>
                        </v-avatar>
                        <v-avatar v-else color="indigo"><v-icon dark>mdi-account-circle</v-icon></v-avatar>
                    </div>
                </template>
                <template v-slot:expanded-item="{ headers, item }">
                    <td :colspan="headers.length">E-Mail: {{ item.email }}</td>
                </template>
            </v-data-table>
        </v-card>
    </v-container>
</template>
<script>
import { mapGetters } from 'vuex'

export default {
    data() {
        return {
            token: '',
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
                avatar: '',
                username: '',
                role: '',
                email: '',
                firstName: '',
                lastName: '',
            },
            roles: ['Admin', 'Umfrageersteller', 'Umfragebearbeiter', 'Teilnehmer'],
            headers: [
                { value: 'avatar', align: 'start', sortable: false, width: '1' },
                { text: 'Username', value: 'username' },
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
            users: [
                {
                    avatar: 'https://avatars0.githubusercontent.com/u/9064066?v=4&s=460',
                    username: 'ymokrane',
                    role: 'Admin',
                    email: 'ymokrane@gmail.com',
                    firstName: 'Mokrane',
                    lastName: 'Yahiatene',
                },
                {
                    avatar: 'default',
                    username: 'jempeerious',
                    role: 'Umfrageersteller',
                    email: 'jempeerious@gmx.de',
                    firstName: 'Jeroshan',
                    lastName: 'Empeerious',
                },
            ],
        }
    },
    computed: {
        ...mapGetters({
            authenticate: 'administration/getToken',
        }),
        formTitle() {
            return this.editedIndex === -1 ? 'Neuer Nutzer' : 'Nutzer bearbeiten'
        },
        formAction() {
            return this.editedIndex === -1 ? 'Nutzer anlegen' : 'Änderungen sichern'
        },
    },
    mounted() {
        this.createToken()
    },
    methods: {
        async createToken() {
            await this.$store.dispatch('administration/setToken')
            this.token = this.authenticate
            const instance = this.$axios.create({
                baseURL: 'http://127.0.0.1:8088/api',
                timeout: 1000,
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
        },
        editUser(item) {
            this.editedIndex = this.users.indexOf(item)
            this.editedUser = Object.assign({}, item)
            this.dialog = true
        },
        deleteUser(item) {
            const index = this.users.indexOf(item)
            confirm('Sind sie sich sicher, dass sie diesen Nutzer löschen möchten?') && this.users.splice(index, 1)
        },
        close() {
            this.dialog = false
            this.$nextTick(() => {
                this.editedUser = Object.assign({}, this.defaultUser)
                this.editedIndex = -1
            })
        },
        sendNewUserData(newUser) {
            const userObj = {
                username: newUser.username,
                firstName: newUser.firstName,
                lastName: newUser.lastName,
                password: 'pwd',
                email: newUser.email,
                role: newUser.role,
            }
            const instance = this.$axios.create({
                baseURL: 'http://127.0.0.1:8088/api',
                timeout: 1000,
                headers: {
                    Authorization: 'Bearer ' + this.token,
                },
            })
            instance.post('/createuser', userObj).catch()
        },
        save() {
            if (this.editedIndex > -1) {
                Object.assign(this.users[this.editedIndex], this.editedUser)
            } else {
                this.defaultUser = Object.assign({}, this.editedUser)
                this.defaultUser.avatar = 'default'
                this.users.push(this.defaultUser)
                this.sendNewUserData(this.defaultUser)
            }
            this.close()
        },
    },
}
</script>
