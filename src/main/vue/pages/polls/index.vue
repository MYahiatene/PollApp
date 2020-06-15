<template>
    <div>
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container v-else-if="getError === undefined">
            <v-layout row wrap>
                <v-container fluid>
                    <v-data-iterator
                        :items="prepareItems"
                        :search="search"
                        :sort-by="sortBy"
                        :sort-desc="sortDesc"
                        hide-default-footer
                    >
                        <template v-slot:header>
                            <v-toolbar class="mb-1">
                                <v-spacer />
                                <v-text-field
                                    v-model="search"
                                    clearable
                                    hide-details
                                    prepend-inner-icon="mdi-magnify"
                                    label="Suchen"
                                ></v-text-field>
                                <v-spacer />
                                <v-btn large color="primary" to="./PollCreation">
                                    <v-icon>mdi-plus</v-icon>
                                </v-btn>
                            </v-toolbar>
                        </template>

                        <v-container>
                            <template>
                                <v-data-table
                                    :headers="headers"
                                    :items="prepareItems"
                                    :search="search"
                                    :custom-filter="filterOnlyCapsText"
                                    class="elevation-1"
                                    :footer-props="footerProps"
                                >
                                    <template v-slot:item.status="{ item }">
                                        <v-icon @click="activatePoll(item)">
                                            {{ item.statusIcon }}
                                        </v-icon>
                                    </template>
                                    <template v-slot:item.action="{ item }">
                                        <v-icon @click="itemAction(item)">
                                            {{ item.actionIcon }}
                                        </v-icon>
                                    </template>
                                    <template v-slot:item.link="{ item }">
                                        <v-icon v-if="item.pollStatus === 1" @click="setLink(item)">
                                            mdi-link-variant
                                        </v-icon>
                                    </template>
                                </v-data-table>
                            </template>
                        </v-container>
                    </v-data-iterator>
                </v-container>
            </v-layout>
        </v-container>
        <v-container v-else>
            <v-card>
                <v-card-title>
                    <v-spacer />
                    <v-icon>
                        mdi-sync-alert
                    </v-icon>
                    Der Umfrato-Server antwortet nicht
                    <v-spacer
                /></v-card-title>
            </v-card>
        </v-container>
    </div>
</template>
<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import AuthGate from '../../components/AuthGate'

export default {
    name: 'Navigation',
    components: { AuthGate },
    data() {
        return {
            itemsList: [],
            search: '',
            filter: {},
            sortDesc: false,
            sortBy: '',
            participationLinks: [],
            contextActions: [
                { title: 'Beantworten', link: '/' },
                { title: 'Bearbeiten', link: '/polls/' },
                { title: 'Aktivieren', link: '/' },
                { title: 'Löschen', link: '/' },
            ],
            headers: [
                { text: '', value: 'status', sortable: false },
                { text: '', value: 'action', sortable: false },
                { text: '', value: 'link', sortable: false },
                { text: 'Umfrage', value: 'pollName' },
                { text: 'Erstellt von', value: 'pollCreator' },
                { text: 'Status', value: 'pollStatusString' },
                { text: 'Kategorienanzahl', value: 'categoryCount' },
                { text: 'Fragenanzahl', value: 'questionCount' },
                { text: 'Anonymitätsgrad', value: 'anonymityString' },
                { text: 'Erstellt am', value: 'creationDate' },
                { text: 'Aktiviert am', value: 'activatedDate' },
                { text: 'Deaktiviert am', value: 'deactivatedDate' },
            ],
            footerProps: {
                itemsPerPageText: 'Umfragen pro Seite',
                itemsPerPageOptions: [10, 20, 50, -1],
                itemsPerPageAllText: 'Alle',
            },
        }
    },
    computed: {
        ...mapGetters({
            items: 'navigation/getPolls',
            isAuthenticated: 'login/isAuthenticated',
            getError: 'navigation/getError',
        }),
        prepareItems() {
            const data = Object.assign([], this.items)
            for (let i = 0; i < data.length; i++) {
                if (this.items[i].anonymityStatus === '1') {
                    data[i].anonymityString = 'Anonym'
                } else if (this.items[i].anonymityStatus === '2') {
                    data[i].anonymityString = 'Teilanonym'
                } else {
                    data[i].anonymityString = 'Nicht anonym'
                }
                if (this.items[i].pollStatus === 0) {
                    data[i].pollStatusString = 'Inaktiv'
                    data[i].actionIcon = 'mdi-pencil'
                    data[i].statusIcon = 'mdi-play'
                } else if (this.items[i].pollStatus === 1) {
                    data[i].pollStatusString = 'Aktiv'
                    data[i].actionIcon = 'mdi-magnify'
                    data[i].statusIcon = 'mdi-stop'
                } else if (this.items[i].pollStatus === 2) {
                    data[i].pollStatusString = 'Beendet'
                    data[i].actionIcon = 'mdi-magnify'
                    data[i].statusIcon = 'mdi-content-duplicate'
                }
                const categories = this.items[i].categoryList
                data[i].categoryCount = categories.length
                data[i].questionCount = 0
                for (let j = 0; j < categories.length; j++) {
                    const count = data[i].questionCount
                    data[i].questionCount = count + categories[j].questionList.length
                }
            }
            return data
        },
    },
    mounted() {
        this.initialize()
        this.initializeLinks()
    },

    methods: {
        ...mapActions({ initialize: 'navigation/initialize' }),
        ...mapMutations({ setPollActive: 'navigation/setPollActive', setPollFinished: 'navigation/setPollFinished' }),

        async initializeLinks() {
            await this.$axios.get('/participationLinks').then((response) => {
                this.participationLinks = response.data
                console.log(response.data)
            })
        },
        activatePoll(item) {
            if (item.pollStatus === 0) {
                if (confirm('Umfrage jetzt veröffentlichen?')) {
                    this.setPollActive(item.pollId)
                }
            } else if (item.pollStatus === 1) {
                if (confirm('Umfrage jetzt beenden?')) {
                    this.setPollFinished(item.pollId)
                }
            } else if (confirm('Umfrage kopieren?')) {
                alert('Tja, das geht noch nicht...')
            }
        },
        itemAction(item) {
            if (item.pollStatus === 0) {
                this.$router.push('/polls/' + item.pollId)
            } else {
                this.$router.push('/eval/' + item.pollId)
            }
        },
        showValue(item, key) {
            alert(item[key])
            return item[key]
        },
        filterOnlyCapsText(value, search, item) {
            return (
                value != null &&
                search != null &&
                typeof value === 'string' &&
                value.toString().toLowerCase().includes(search.toLowerCase())
            )
        },
        setLink(item) {
            for (let i = 0; i < this.participationLinks.length; i++) {
                if (this.participationLinks[i].pollId === item.pollId) {
                    navigator.clipboard.writeText(
                        'http://localhost:8080/participant/' + this.participationLinks[i].participationLink
                    )
                    alert(
                        'Link kopiert: "localhost:8080/participant/' +
                            this.participationLinks[i].participationLink +
                            '"'
                    )
                }
            }
        },
    },
}
</script>
