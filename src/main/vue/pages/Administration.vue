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
                Nutzerdatenbank
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
                                    <v-col v-if="editedIndex === -1" cols="12" sm="6" md="4">
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
                <template v-slot:expanded-item="{ headers, item }">
                    <v-container>E-Mail: {{ item.email }}</v-container>
                    <v-container>Name: {{ item.firstName }} {{ item.lastName }}</v-container>
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
        }),
        formTitle() {
            return this.editedIndex === -1 ? 'Neuer Nutzer' : 'Nutzer bearbeiten'
        },
        formAction() {
            return this.editedIndex === -1 ? 'Nutzer anlegen' : 'Änderungen sichern'
        },
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
            console.log('users', this.users)
        },
        editUser(item) {
            this.editedIndex = this.users.indexOf(item)
            this.editedUser = Object.assign({}, item)
            this.dialog = true
        },
        deleteUser(item) {
            const index = this.users.indexOf(item)

            const instance = this.$axios.create({
                baseURL: 'http://127.0.0.1:8088/api',
                timeout: 1000,
                headers: {
                    Authorization: 'Bearer ' + this.token,
                },
            })
            confirm('Sind sie sich sicher, dass sie diesen Nutzer löschen möchten?') &&
                this.users.splice(index, 1) &&
                instance
                    .put('/deleteUser', {
                        username: item.username,
                    })
                    .catch((error) => {
                        console.log(error)
                    })
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
            instance.post('/createUser', userObj).catch()
        },
        editUserData(editUser) {
            const userObj = {
                username: editUser.username,
                firstName: editUser.firstName,
                lastName: editUser.lastName,
                email: editUser.email,
                role: editUser.role,
            }
            const instance = this.$axios.create({
                baseURL: 'http://127.0.0.1:8088/api',
                timeout: 1000,
                headers: {
                    Authorization: 'Bearer ' + this.token,
                },
            })
            instance.put('/editUser', userObj).catch()
        },
        save() {
            if (this.editedIndex > -1) {
                Object.assign(this.users[this.editedIndex], this.editedUser)
                this.editUserData(this.editedUser)
            } else {
                this.defaultUser = Object.assign({}, this.editedUser)
                this.users.push(this.defaultUser)
                this.sendNewUserData(this.defaultUser)
            }
            this.close()
        },
    },
}
</script>
