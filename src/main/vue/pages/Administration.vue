<template>
    <div>
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-layout v-else>
            <v-flex class="text-center">
                <v-card class="pa-5">
                    <h1>Administrationsseite</h1>
                </v-card>
            </v-flex>
        </v-layout>
    </div>
</template>
<template>
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
                        <v-spacer></v-spacer>
                        <v-btn color="blue darken-1" text @click="close">Abbrechen</v-btn>
                        <v-btn color="blue darken-1" text @click="save">Sichern</v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>
        </v-card-title>
        <v-data-table :headers="headers" :items="users" :search="search">
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
        </v-data-table>
    </v-card>
</template>
<script>
export default {
    data() {
        return {
            dialog: false,
            search: '',
            sortBy: '',
            editedIndex: -1,
            editedUser: {
                username: '',
                role: '',
            },
            defaultUser: {
                avatar: '',
                username: '',
                role: '',
            },
            roles: ['Admin', 'Umfrageersteller', 'Umfragebearbeiter', 'Keine Rolle'],
            roleItems: [
                { role: 'Admin' },
                { role: 'Umfrageersteller' },
                { role: 'Umfragebearbeiter' },
                { role: 'Keine Rolle' },
            ],
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
            ],
            users: [
                {
                    avatar: 'https://avatars0.githubusercontent.com/u/9064066?v=4&s=460',
                    username: 'ymokrane',
                    role: 'Admin',
                },
                { avatar: 'default', username: 'jempeerious', role: 'Umfrageersteller' },
            ],
        }
    },
    computed: {
        formTitle() {
            return this.editedIndex === -1 ? 'Neuer Nutzer' : 'Nutzer bearbeiten'
        },
    },
    methods: {
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

        save() {
            if (this.editedIndex > -1) {
                Object.assign(this.users[this.editedIndex], this.editedUser)
            } else {
                this.defaultUser = Object.assign({}, this.editedUser)
                this.defaultUser.avatar = 'default'
                this.users.push(this.defaultUser)
            }
            this.close()
        },
    },
}
</script>
