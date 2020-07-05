<template>
    <v-dialog overlay-color="background" v-model="dialog" width="800" overlay-opacity="0.75">
        <template v-slot:activator="{ on }">
            <v-btn color="primary" v-on="on" class="ma-2">
                Sessions
            </v-btn>
        </template>
        <v-card class="pa-3">
            <v-card-title>
                Sessionmanagement
            </v-card-title>
            <v-card-text>
                Hier können Sie alle Auswertungssessions zu dieser Umfrage verwalten und neue erstellen.
            </v-card-text>
            <v-data-iterator
                :items="sessions"
                :search="search"
                :sort-by="sortBy"
                :sort-desc="sortDesc"
                no-data-text="Es gibt noch keine Sessions zu dieser Umfrage"
                no-results-text="Keine passenden Sessions gefunden"
                hide-default-footer
            >
                <template v-slot:header>
                    <v-toolbar class="pa-1">
                        <v-text-field
                            class="pd-n2 ma-2"
                            dense
                            v-model="search"
                            clearable
                            hint="Vergangene Sessions durchsuchen"
                            prepend-inner-icon="mdi-magnify"
                            label="Durchsuchen"
                        ></v-text-field>
                        <v-text-field
                            class="pd-n2 ma-2"
                            v-model="sessionTitle"
                            dense
                            clearable
                            label="Session speichern"
                            hint="Geben Sie dieser Session einen Namen"
                            prepend-inner-icon="mdi-content-save"
                            :error="titleErrorMessage.length > 0"
                            :error-messages="titleErrorMessage"
                            :rules="sessionTitleRule"
                            @input="titleErrorMessage = ''"
                        ></v-text-field>
                        <v-spacer />
                        <v-btn color="primary" @click="saveThisSession">
                            <v-icon>mdi-plus</v-icon>
                        </v-btn>
                    </v-toolbar>
                </template>
                <template>
                    <v-data-table
                        :headers="headers"
                        :items="sessions"
                        :search="search"
                        :custom-filter="filterOnlyCapsText"
                        class="elevation-1"
                        :footer-props="footerProps"
                    >
                        <template v-slot:item.saveIcon="{ item }">
                            <v-icon @click="saveThisSession(item)">
                                mdi-content-save
                            </v-icon>
                        </template>
                        <template v-slot:item.deleteIcon="{ item }">
                            <v-icon @click="deleteOneSession(item)">
                                mdi-delete
                            </v-icon>
                        </template>
                        <template v-slot:item.loadIcon="{ item }">
                            <v-icon @click="loadOneSession(item)">
                                mdi-chevron-double-right
                            </v-icon>
                        </template>
                    </v-data-table>
                </template>
            </v-data-iterator>
        </v-card>
    </v-dialog>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
export default {
    name: 'SessionManager',
    data() {
        return {
            search: '',
            sortDesc: false,
            sortBy: '',
            sessionTitle: '',
            choosenSessionTitle: '',
            titleErrorMessage: '',
            headers: [
                { text: 'Name', value: 'sessionTitle' },
                { text: 'Zuletzt bearbeitet', value: 'lastEdited' },
                { text: 'von', value: 'lastUsername' },
                { text: '', value: 'saveIcon', sortable: false },
                { text: '', value: 'deleteIcon', sortable: false },
                { text: '', value: 'loadIcon', sortable: false },
            ],
            footerProps: {
                itemsPerPageText: 'Sessions pro Seite',
                itemsPerPageOptions: [10, 20, 50, -1],
                itemsPerPageAllText: 'Alle',
            },
            sessionTitleRule: [(v) => this.sessionTitleTaken(v) || 'Dieser Name ist bereits vergeben'],
        }
    },
    computed: {
        ...mapGetters({
            getSessions: 'evaluation/getSessions',
            getUsername: 'login/getUsername',
            currentSession: 'evaluation/getCurrentSession',
        }),
        sessions() {
            const sessions = []
            const data = this.getSessions
            for (let i = 0; i < data.length; i++) {
                sessions.push({
                    sessionId: data[i].sessionId,
                    sessionTitle: data[i].sessionTitle,
                    lastEdited:
                        data[i].lastEdited.slice(8, 10) +
                        '.' +
                        data[i].lastEdited.slice(5, 7) +
                        '.' +
                        data[i].lastEdited.slice(0, 4),
                    lastUsername: data[i].lastUsername,
                    saveIcon: '',
                    deleteIcon: '',
                    loadIcon: '',
                })
            }
            return sessions
        },
    },
    methods: {
        ...mapActions({
            loadSessions: 'evaluation/loadSessions',
            loadSession: 'evaluation/loadSession',
            deleteSession: 'evaluation/deleteSession',
            saveSession: 'evaluation/saveSession',
            updateData: 'evaluation/updateData',
        }),
        sessionTitleTaken(title) {
            for (let i = 0; i < this.sessions.length; i++) {
                if (title === this.sessions[i].sessionTitle) {
                    return false
                }
            }
            return true
        },
        async saveThisSession(item) {
            console.log('saveThisSession()')
            if (item.sessionId) {
                const payload = {
                    sessionId: item.sessionId,
                    sessionTitle: item.sessionTitle,
                    lastUsername: this.getUsername,
                }
                await this.saveSession(payload)
                await this.loadSessions()
            } else if (this.sessionTitle.length < 1) {
                this.titleErrorMessage = 'Geben Sie der Session einen eindeutigen Namen'
            } else {
                const payload = {
                    sessionId: -1,
                    sessionTitle: this.sessionTitle,
                    lastUsername: this.getUsername,
                }
                await this.saveSession(payload)
                this.sessionTitle = ''
                await this.loadSessions()
            }
        },

        async loadOneSession(item) {
            console.log('loadOneSession()')
            await this.loadSession(item.sessionId)
            await this.updateData()
            this.$emit('close-event')
            this.dialog = false
        },

        async deleteOneSession(item) {
            console.log('deleteOneSession()')
            await this.deleteSession(item.sessionId)
            await this.loadSessions()
            await this.updateData()
        },

        filterOnlyCapsText(value, search, item) {
            return (
                value != null &&
                search != null &&
                typeof value === 'string' &&
                value.toString().toLowerCase().includes(search.toLowerCase())
            )
        },
    },
}
</script>

<style scoped></style>
