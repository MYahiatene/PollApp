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
                        no-data-text="Es gibt noch keine Umfragen"
                        no-results-text="Keine passenden Umfragen gefunden"
                        hide-default-footer
                    >
                        <template v-slot:header>
                            <v-toolbar class="mb-1">
                                <v-btn icon color="primary" @click="initialize">
                                    <v-icon> mdi-refresh</v-icon>
                                </v-btn>
                                <v-btn large color="primary" @click="initializeDatabase">
                                    <v-icon :color="progressA">mdi-auto-fix</v-icon>
                                </v-btn>
                                <v-spacer />
                                <v-btn large color="secondary" @click="answerPoll">
                                    <v-icon :color="progressB">mdi-auto-fix</v-icon>
                                </v-btn>
                                <v-spacer />
                                <v-text-field
                                    v-model="search"
                                    clearable
                                    hide-details
                                    prepend-inner-icon="mdi-magnify"
                                    label="Suchen"
                                ></v-text-field>
                                <v-spacer />
                                <v-tooltip bottom>
                                    <template v-slot:activator="{ on, attrs }">
                                        <v-btn
                                            large
                                            color="primary"
                                            to="./PollCreation/0"
                                            v-bind="attrs"
                                            :disabled="isCreator === false"
                                            v-on="on"
                                        >
                                            <v-icon>mdi-plus</v-icon>
                                        </v-btn>
                                    </template>
                                    <span> Neue Umfrage erstellen</span>
                                </v-tooltip>
                                <template>
                                    <v-dialog
                                        v-model="exportWidgetDialog"
                                        overlay-color="background"
                                        width="800"
                                        overlay-opacity="0.75"
                                    >
                                        <exportWidget :exportResult="false" @done="exportWidgetDialog = false" />
                                    </v-dialog>
                                </template>
                                <template>
                                    <v-dialog v-model="qrDialog" overlay-color="background" overlay-opacity="0.75">
                                        <qrGenerator />
                                    </v-dialog>
                                </template>
                                <import-widget @done="initialize" />
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
                                    multi-sort
                                    :expanded.sync="expanded"
                                    show-expand
                                    single-expand
                                    no-data-text="Es gibt noch keine Umfragen"
                                    no-results-text="Keine passenden Umfragen gefunden"
                                    loading-text="Lade Umfragen"
                                    :footer-props="footerProps"
                                >
                                    <template v-slot:top>
                                        <v-toolbar flat>
                                            <v-toolbar-title>Alle Umfragen</v-toolbar-title>
                                        </v-toolbar>
                                    </template>
                                    <template v-slot:item.actions="{ item }">
                                        <v-tooltip bottom>
                                            <template v-slot:activator="{ on, attrs }">
                                                <v-btn icon color="primary" v-bind="attrs" v-on="on">
                                                    <v-icon @click="itemStatusAction(item)">
                                                        {{ item.statusIcon }}
                                                    </v-icon>
                                                </v-btn>
                                            </template>
                                            <span> {{ item.statusText }}</span>
                                        </v-tooltip>
                                        <v-tooltip bottom>
                                            <template v-slot:activator="{ on, attrs }">
                                                <v-btn icon color="primary" v-bind="attrs" v-on="on">
                                                    <v-icon @click="itemAction(item)">
                                                        {{ item.actionIcon }}
                                                    </v-icon>
                                                </v-btn>
                                            </template>
                                            <span> {{ item.actionText }}</span>
                                        </v-tooltip>
                                    </template>
                                    <template v-slot:expanded-item="{ headers, item }">
                                        <td :colspan="headers.length">
                                            <v-tooltip bottom>
                                                <template v-slot:activator="{ on, attrs }">
                                                    <v-btn
                                                        icon
                                                        color="primary"
                                                        :disabled="item.pollStatus === 3 || item.anonymityStatus < 2"
                                                        v-bind="attrs"
                                                        v-on="on"
                                                    >
                                                        <v-icon @click="inviteAction(item)">
                                                            mdi-account-plus
                                                        </v-icon>
                                                    </v-btn>
                                                </template>
                                                <span> Teilnehmer einladen</span>
                                            </v-tooltip>
                                            <v-tooltip bottom>
                                                <template v-slot:activator="{ on, attrs }">
                                                    <v-btn
                                                        icon
                                                        color="primary"
                                                        :disabled="
                                                            item.pollStatus < 1 ||
                                                            item.pollStatus > 2 ||
                                                            item.anonymityStatus < 2
                                                        "
                                                        v-bind="attrs"
                                                        v-on="on"
                                                    >
                                                        <v-icon @click="reminderAction(item)">
                                                            mdi-message-alert
                                                        </v-icon>
                                                    </v-btn>
                                                </template>
                                                <span> Teilnehmer benachrichtigen</span>
                                            </v-tooltip>
                                            <v-tooltip bottom>
                                                <template v-slot:activator="{ on, attrs }">
                                                    <v-btn icon color="primary" v-bind="attrs" v-on="on">
                                                        <v-icon @click="exportPoll(item)">
                                                            mdi-cloud-download
                                                        </v-icon>
                                                    </v-btn>
                                                </template>
                                                <span> Umfrage exportieren </span>
                                            </v-tooltip>
                                            <v-tooltip bottom>
                                                <template v-slot:activator="{ on, attrs }">
                                                    <v-btn icon color="primary" v-bind="attrs" v-on="on">
                                                        <v-icon @click="copyPoll(item)">
                                                            mdi-content-copy
                                                        </v-icon>
                                                    </v-btn>
                                                </template>
                                                <span> Umfrage kopieren</span>
                                            </v-tooltip>
                                            <v-tooltip bottom>
                                                <template v-slot:activator="{ on, attrs }">
                                                    <v-btn
                                                        icon
                                                        color="primary"
                                                        :disabled="item.pollStatus < 1 || item.pollStatus > 2"
                                                        v-bind="attrs"
                                                        v-on="on"
                                                    >
                                                        <v-icon @click="setLink(item)">
                                                            mdi-link-variant
                                                        </v-icon>
                                                    </v-btn>
                                                </template>
                                                <span> Links kopieren</span>
                                            </v-tooltip>
                                            <v-tooltip bottom>
                                                <template v-slot:activator="{ on, attrs }">
                                                    <v-btn
                                                        icon
                                                        color="primary"
                                                        :disabled="
                                                            item.pollStatus < 1 ||
                                                            item.pollStatus > 2 ||
                                                            item.anonymityStatus !== '1'
                                                        "
                                                        v-bind="attrs"
                                                        v-on="on"
                                                    >
                                                        <v-icon @click="generateQR(item)">
                                                            mdi-qrcode
                                                        </v-icon>
                                                    </v-btn>
                                                </template>
                                                <span> QR generieren</span>
                                            </v-tooltip>
                                            <v-tooltip bottom>
                                                <template v-slot:activator="{ on, attrs }">
                                                    <v-btn
                                                        icon
                                                        color="primary"
                                                        :disabled="!item.isSeries"
                                                        v-bind="attrs"
                                                        v-on="on"
                                                    >
                                                        <v-icon @click="abortSeries(item)">
                                                            mdi-cancel
                                                        </v-icon>
                                                    </v-btn>
                                                </template>
                                                <span> Serienumfrage abbrechen </span>
                                            </v-tooltip>
                                            <v-tooltip bottom>
                                                <template v-slot:activator="{ on, attrs }">
                                                    <v-btn
                                                        icon
                                                        color="primary"
                                                        :disabled="item.pollStatus === 2"
                                                        v-bind="attrs"
                                                        v-on="on"
                                                    >
                                                        <v-icon @click="deletePoll(item)">
                                                            mdi-delete
                                                        </v-icon>
                                                    </v-btn>
                                                </template>
                                                <span> Umfrage löschen </span>
                                            </v-tooltip>
                                        </td>
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
import ImportWidget from '../../components/importWidget'
import ExportWidget from '../../components/exportWidget'
import qrGenerator from '../../components/qrGenerator'
export default {
    name: 'Navigation',
    components: {
        ExportWidget,
        ImportWidget,
        AuthGate,
        qrGenerator,
    },
    data() {
        return {
            hovered: false,
            participantNr: 2,
            progressColorA: '#FF0000',
            progressColorB: '#FF0000',
            itemsList: [],
            expanded: [],
            search: '',
            filter: {},
            sortDesc: false,
            sortBy: '',
            participationLinks: [],
            showAll: false,
            exportWidgetDialog: false,
            exportPollId: -1,
            qrDialog: false,
            qrLink: '',
            qrTitle: '',
            headers: [
                { text: '', value: 'actions', sortable: false },
                { text: '', value: 'data-table-expand' },
                { text: 'Umfrage', value: 'pollName' },
                { text: 'Erstellt von', value: 'pollCreator' },
                { text: 'Status', value: 'pollStatusString' },
                { text: 'Beantwortet von', value: 'participantCount' },
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
            isCreator: 'account/getIsCreator',
            items: 'navigation/getPolls',
            isAuthenticated: 'login/isAuthenticated',
            getError: 'navigation/getError',
        }),
        progressA() {
            return this.progressColorA
        },
        progressB() {
            return this.progressColorB
        },
        prepareItems() {
            const data = Object.assign([], this.items)
            for (let i = 0; i < data.length; i++) {
                data[i].id = data[i].pollId
                if (this.items[i].anonymityStatus === '1') {
                    data[i].anonymityString = 'Anonym'
                } else if (this.items[i].anonymityStatus === '2') {
                    data[i].anonymityString = 'Teilanonym'
                } else {
                    data[i].anonymityString = 'Nicht anonym'
                }
                if (this.items[i].pollStatus === 0) {
                    data[i].pollStatusString = 'Bearbeitung'
                    data[i].statusIcon = 'mdi-check'
                    data[i].statusText = 'Umfrage bereit machen'
                    data[i].actionIcon = 'mdi-pencil'
                    data[i].actionText = 'Umfrage bearbeiten'
                } else if (this.items[i].pollStatus === 1) {
                    data[i].pollStatusString = 'Bereit'
                    data[i].statusIcon = 'mdi-play'
                    data[i].statusText = 'Umfrage aktivieren'
                    data[i].actionIcon = 'mdi-pencil'
                    data[i].actionText = 'Umfrage wieder bearbeiten'
                } else if (this.items[i].pollStatus === 2) {
                    data[i].pollStatusString = 'Live'
                    data[i].statusIcon = 'mdi-stop'
                    data[i].statusText = 'Umfrage deaktivieren'
                    data[i].actionIcon = 'mdi-magnify'
                    data[i].actionText = 'Umfrage analysieren'
                } else if (this.items[i].pollStatus === 3) {
                    data[i].pollStatusString = 'Deaktiviert'
                    data[i].statusIcon = 'mdi-content-copy'
                    data[i].statusText = 'Umfrage kopieren'
                    data[i].actionIcon = 'mdi-magnify'
                    data[i].actionText = 'Umfrage analysieren'
                }
            }
            return data
        },
    },
    mounted() {
        this.initialize()
    },
    methods: {
        ...mapActions({
            initialize: 'navigation/initialize',
            updatePollStatus: 'navigation/updatePollStatus',
            reEditPoll: 'navigation/reEditPoll',
        }),
        ...mapMutations({
            setPollActive: 'navigation/setPollActive',
            setPollFinished: 'navigation/setPollFinished',
            splicePolls: 'navigation/splicePolls',
            setPollIndex: 'navigation/setPollIndex',
            setQrLink: 'navigation/setQrLink',
            setQrTitle: 'navigation/setQrTitle',
        }),
        itemStatusAction(item) {
            if (item.pollStatus === 0) {
                if (confirm('Umfrage als Bereit markieren?')) {
                    this.updatePollStatus(item.pollId)
                }
            } else if (item.pollStatus === 1) {
                if (confirm('Umfrage jetzt veröffentlichen?')) {
                    this.updatePollStatus(item.pollId)
                }
            } else if (item.pollStatus === 2) {
                if (confirm('Umfrage jetzt beenden?')) {
                    this.updatePollStatus(item.pollId)
                }
            } else if (confirm('Umfrage kopieren?')) {
                this.copyPoll(item)
            }
        },
        itemAction(item) {
            if (item.pollStatus === 0) {
                this.$router.push('/polls/' + item.pollId)
            } else if (item.pollStatus === 1) {
                if (confirm('Umfrage noch einmal bearbeiten?')) {
                    this.reEditPoll(item.pollId)
                }
            } else {
                this.$router.push('/eval/' + item.pollId)
            }
        },
        inviteAction(item) {
            this.$router.push('/invite/' + item.pollId)
        },
        reminderAction(item) {
            this.$router.push('/remind/' + item.pollId)
        },
        filterOnlyCapsText(value, search, item) {
            return (
                value != null &&
                search != null &&
                typeof value === 'string' &&
                value.toString().toLowerCase().includes(search.toLowerCase())
            )
        },
        exportPoll(item) {
            console.log(item)
            this.setPollIndex(item.pollId)
            this.exportWidgetDialog = true
        },
        copyPoll(item) {
            this.$router.push('/PollCreation/' + item.pollId)
        },
        setLink(item) {
            let links = ''
            for (let i = 0; i < item.participationLinks.length; i++) {
                links += item.participationLinks[i].generatedParticipationLink
                if (i + 1 !== item.participationLinks.length) {
                    links += ', '
                }
            }
            navigator.clipboard.writeText(links)
            alert('Link(s) kopiert: ' + links.slice(0, 120) + (links.length > 117 ? '...' : ''))
        },
        generateQR(item) {
            let links = ''
            for (let i = 0; i < item.participationLinks.length; i++) {
                links = item.participationLinks[i].generatedParticipationLink
            }
            this.setQrTitle(item.pollName)
            this.setQrLink(links)
            this.qrDialog = true
        },
        abortSeries(item) {
            const del = confirm('Sind sie sich sicher, dass sie diese Serienumfrage abbrechen möchten?')
            if (del) {
                this.$axios.put('/abortPoll/' + item.pollId)
            }
        },
        deletePoll(item) {
            const index = this.items.indexOf(item)
            const del = confirm('Sind sie sich sicher, dass sie diese Umfrage entgültig löschen möchten?')
            if (del) {
                this.splicePolls(index)
                this.$axios.put('/deletepoll', { pollId: item.pollId })
            }
        },
        async initializeDatabase() {
            const today = new Date()
            const dd = String(today.getDate()).padStart(2, '0')
            const mm = String(today.getMonth() + 1).padStart(2, '0') // January is 0!
            const yyyy = today.getFullYear()
            const time =
                (today.getHours() < 10 ? '0' : '') +
                today.getHours() +
                ':' +
                (today.getMinutes() < 10 ? '0' : '') +
                today.getMinutes()
            const poll = {
                pollCreator: 'Jan',
                anonymityStatus: '3',
                pollName: 'Beispielumfrage',
                activated: false,
                deactivated: false,
                creationDate: dd + '.' + mm + '.' + yyyy + '&' + time,
                activatedDate: dd + '.' + mm + '.' + yyyy + '&' + time,
                deactivatedDate: '11' + '.' + mm + '.' + yyyy + '&' + time,
                pollStatus: 0,
                categoryChange: true,
                visibility: true,
                backgroundColor: '#555555',
                fontColor: '#fe7312',
                level: -1,
                repeat: -1,
                day: [],
                week: [],
                month: [],
                ownDesign: true,
                checkLeapYear: false,
                stoppingReason: 1,
                repeatUntil: '10',
                logo:
                    'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAIAAAAiOjnJAAAAAXNSR0IArs4c6QAAAARnQU1' +
                    'BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAA4dSURBVHhe7Z2/ixzHEsf93zhz5siZM2WOlL3sZc4UOVMgkAMjHF' +
                    'xghAIfOFAgsAMbFFxgkILj4eAZDFIgjAMJFFwgxAUyvGDf99Sr9lzPdHX1r+mqmfpQGKPb2zv1fqaqunp29dHBCHj39vDDv' +
                    'w+3P/4nHtw8XPx5/KrBw8Sa8f2/rlnl4utPD6+fHR9gMDCxrvPy91ApH+ZWDibWBBRBVL3Ap2mYW2xMrA8krXJxcsP6LQ4m' +
                    '1gcWW6vFQN766z/H7zIimFjv+eOX0J5k2FaRZPdizYcL/EBZ/N/fx+cxrrN7sfgVcDEsb0XYt1holQJRCsLa+SV2LNabV4d' +
                    'vPw8tmcadTw5n9w7ffBb++TzMrRl7FStpFeL89OqRaMKe3r+SLPhqEObWdfYnFtrtR7dCLeYBky4vjt8CXjy5mjIEjwnCxq' +
                    'cT9ifWT1+FQswDVv3xy/HxHjRknLz18vfj4/fNnsRiThZ+PbmWq6Zw8hbCtoo7EguucE5s/vvj8fExkJCsnWewD7HQ+iRbd' +
                    'UTSKgffrR2PT7cuFhIVc7DOtMrBdGvHNXHTYsEqTqJCS55llQMdG+fJ97pV3LRYnFxV88Lza+L+toobFYu5AUSuqrwBhukW' +
                    'YmdlcYtiwSrOBnBxWFUAftz5qbXzAZsTC3WNc8MCrHrz6vgtTeCMTxG7yVsbEotZ/hCtclUAc3y6j5ZrQ2IxExXKVmywXg9' +
                    'zH4rYeurailjMe4t7JKoAfju/6em8frH4G8CCYVUZWW5ttCzqF4tZAVfIVVOgO+cuLhdbLIvKxWLeW7yyVR5mO49A6trWgF' +
                    '6zWJy7QNesgIvwyyJiQ6lLrVic/dejWyJepyy3tjJEVStWsmFHruo3VsiFP51HbCJvKRQLunCsGtVXEfBTFzqzsRW8Gm1ic' +
                    'SogrGp7XNMQt1vkd/RqU5c2sZTmqgDmwSICClbefzEIPWJxBqE/fSWor6LhTyIQCrsuJWLBKixusNxBSK6Ai+AaOLvHTV3a' +
                    'yqISsTjjdfkVcJGsIaqeSURPsV4/u9ra/HpSGz/fDpc4CFz0qvdQLnVx9NJz8fQRi9MPNYxHt678g1uqxz/Q6+GX4V8tCD0' +
                    'FsY9YlR86VRPffn4t2xEBEaUdz6HSJYcpSgpiB7Fexj/RWmBgT+A9k5AMOENUDQWxg1jnp+FCKIog4Q3JamgkHt8Nf7FpaE' +
                    'haHcTC6xEshPbwWW0aXZ1L1sTnZ8dHSqWDWE/vh6uw4ZhW0rae0TURP1E2HcR68SRchf2E86yVZOilguf3sUexOFsbBLbW7' +
                    'lr3kZxX6Yr6AkqsJJ5KNh3EApytTTCSwXLjlQgeE8SdT6662unr5IPzOX1ygl9A8Zjge13gz2XTRyzAcevr9x/IwZ+m0tvs' +
                    'N68Ovz08vmB0MMfcq8VibkNAu9gFg6/KpptYAMYkayJe4GSiQjQ/tLm8uPYS6kp4iF2LBTh5KxmwaoWRYJDwpGW1IPYuFki' +
                    'O++hARz/qFqsgq/mQ4Bx+Ddn0Fwsw94nzQK4aZRXN1LkhnplYRwpq4joVsAnes9UkE3+qs5ZYADWR/67zgRWwktUKqOyrbk' +
                    'WxHC+esNyCgttj6lz9ngZPIpjVxQLMm3H13NRWAvL3+ek/nrnIuo8NjxfMCLEA8zwR/ul881MhWbfdym6zBomVtU98cPOq9' +
                    '988r5+Ff/FkCL55ZpBYIPfuGui14coICt4lILgajhOr4O4aVEb+rQG6KEhXCBNrgbKpKRoLpWMIGiJdwZ7YQp3dO367PMaJ' +
                    'BYrvNcXuCbuqzUCkqzvv394dW6hvPhPbvw8Vq+Ze0+++ED56zoB4+4mb5xGfiCm1fx8qVlk19LGZdh7FLviruXDpCmChYgN' +
                    'VqW3WULFAbE0hDfMAZANlMbYI0xYK/x981YWJtUxsTfHnaNKZ+Ux7WYwN3KfSEAslEsFiARQCplt6y+LL+BvHTaxykuuFvM' +
                    'W8b1jp2SLRuZtY5cTWKzgIY6YujRNUYoI1vc3fxMojtl6IYCPNd0vRuTU9wZqOgk2sPIgZ6XzJsPvDRczZLWo5t05OsDwmV' +
                    'h7EjDS2ZPzdovyOHvvZ4Hd24SdYnphYUm+eGS0WMSMlrkX+blFyZSTqIDJZAJHaRd6jPFosELsW6RPWrHdnyExdRNs+/20L' +
                    'UvtQBIhVfMKa9e4MaRtGYnz18MvjY6aUpfZxCBCr8oQVl7I6t+g2cV4HHbHUbmItg2ux8oQVbnG2igg8rO1nQJRBFMF52+4' +
                    'xsbKpP2FFDsCTMPXCXmzguTXRsyOCKcMUEyubVkvm9OJURrg1qiwSsysiXYHYKomcOGxLLAe/Mq6/W6S7K3pwoGriIFus4m' +
                    'uRn7pWvtyJ7urx3eNjYqiaOMgQq9O1yNwwrvaq0N1VbDPoUTVxkCFWv2uRWRaRt1aoicXdlSeW2k2sZbpei3Rb4wP+dW3n6' +
                    'WMoZmI2sbLpumR/8f6p3H5u0XITI4YAEyub3kvG3yr2eHcG0bMj+FXYxMpmhSXLmnI13CrSPfviyWAMEyub1ZaMmbr45SkJ' +
                    '3bNnbRpMrGzWXDKkLs4bNJqMT/GzsOUMntlH7sGliZXN+kvGmXJVtvOtenaPiZXNkCXjuAUzivst+qMfC9KhiZVNbMnQbne' +
                    'F03KV1cTnZ+HzTCOrZ/eYWNnETnVW+KQeFKzkXc65NTF5V37ZbWEmVjZjP6mHM0Hl18SkVfhZsLkAEysbvGaV95FW0qom0g' +
                    '07AlYVn6ybWCWgnQrWy8VqqwYnHt0Kf3oQyZpID9nx1bJc5TCxSpCwakicdL5BEG4R771BIFdVDsZMrBKErFqyQ0IsutWpY' +
                    'Z9iYpUgZ9XevY3WZR+BW8nWqsm7g0ysEmKr1nuUtUhWTcR/6VloqxfexCph4ChrEU5NPLmR6NYRaK3wVE0wsUoYO8paxH1w' +
                    'UnLERUfDt9CYWCUMH2XF4BwpxqJJa+UxsQoZPsqKUeZWW6uAiVWI5IWjD5XnUXbMTGNiFSJ84Yhb9oLAhrH+JsE5JlYhwhf' +
                    'uNXn3ug8UTWxEemBiFRJbuJXfCB+DHlb56LeHNbEKiS0cYtTEwUOfA06DOEysxMQqJDYjRYxdu8sLbrpy0cktE6sQ7OqDJf' +
                    'MxcO04I/h59HDLxCqEOKEbtXaQo8AqF83dMrHKkbN2KH/Jc8BktHXLxCpHyNolb4Nx8eDm4beHiaF8wy2tiVWOkLXjtOrIR' +
                    'm4KmjzwaXUObWKVM3ztmBUwmILSbn33RZtBfOzf3jGx0owV6/kZqwKeny7cYkW7Vd9s4duD5/TR/LS7GhPrA2iDmP+UK/Eq' +
                    'xn55F5XNFvGpNT3OJeswsd7DnH8iIdG5gZjDuahJLbGV6XEbRTUmFnv+yXm3KjGHc1FTEIdccqXsXiymVX4DmCT5bv3ighj' +
                    'bUphYLFYTC69u8n3PLoINYBIURIgYPMk0Cgoi0bmbWCxiYjW/c4bTqkOpxQ1gErrZwt8lt90mOnd8SR56xEK0unMGzxMbCE' +
                    '0DVhXvtpo3W7FlwS9Z4H1/5InV+84Z5kwBL1jluDz5jwtn5a2YWEPezctAnlhEEakXi2kVftBlxWfCeN69TeQtfn2PidXkY' +
                    'uuAPLGIIlKziPxbFdpOsZH2gucPglnfTawGtF3ErN1fW6tAstliHiOqmjWAHYjFLH/1Z3kxOM0WXRC1zRqAKrFyJw5ocZjl' +
                    'D7mq0xu2HMlmi94oaJs1AJFiERtD/k4NTdWDm+G3zwOPQflbYcdON1v0NRO70qTOGoBIseo3hqgddIZwwT+oqSfZbBHtXSz' +
                    'vSp01AJFiEa9BUiwoJaT8zaGPEWNJS2GDBUSKBWLJn1jKrI6q7KCmHvoYcfEGCoUNFtiQWMw3lPbb/THJbbZiV4vgBgtsQi' +
                    'z+8HP98jcn2WxNR6ZEHRTcYAFtYs0vaOZd6ghYxd9UdoVOWtOLR2cdBFLF4kwcYBhz+InAq9Xk+K8JdNKaXjyxC0x2HQRSx' +
                    'UpOHNDkMpsqvAbND2rqoXeI/uKJiSW7DgKpYhHX9M+3Mzqqx3fXm1TlEpMGgaTlfu3Y39RdXYKRKhYg1p0ZQjqqGMm7TIlP' +
                    '5DKxyqkRS2b5C6A7LQRxJCW7cwebE0t4+QtI3viwGPg7yu7cwbbEwoprUcpDjx4WQ3znDrYlFmoHvqsyUEPXHM0nC+I8xNd' +
                    'BIFgsVLRgQdeMRUc7OZeVtDTUQSBSLP5x8pCYOtdEtaykpaEOApFiZX0+sYQI0htsy231+F28hjoI5IlFDG90BZJQVj7juK' +
                    'WkDgJ5YhHHrkoD+QzScEi6paQOAnliEcfPqsOVy2QOQ3+JSyvWDOAZlCBPLPqgYxuRzGFqjwg98sTib5FQF1yz3CrwhMR9w' +
                    '80Dei0mMPxJ8Egf+CWVIE8swGljEa0+fGbK5cU11Xx0dS4wjOgylWwJgUixgGs18IoSAy18dU0C53qoBsMu/rx68uDPXejZ' +
                    'EgKpYnmIlgtX+VgW0xv/ptbFOLkR7dz1bAmBeLFiLRcuXzm3GgcgryDdNs9nsFYP4sUC85YLVgm/iQ/A+7bl0sRqj2+5EPg' +
                    'fsblqji+X9ZINL/05KBFrGzB3u4shufQvYWKti0+9uYap6tyBiTUIZxj/bhm4qAoTayj84mhiGXkgdT29n+7rVXXuwMSSAR' +
                    'pzoixq69yBiSWGWFlUMbSbYWJJQukoeAkTSxh6R8HXMbGMLphYRhdMLKMLJpbRBRPL6IKJZXTBxDK6YGIZXTCxjC6YWEYXT' +
                    'CyjCyaW0QUTy+iCiWV0wcQyumBiGV0wsYwumFhGF0wsowsmltEFE8vogolldMHEMrpgYhldMLGMLphYRhdMLKMLJpbRBRPL' +
                    '6IKJZXTBxDI6cDj8H7arjjyoRWNPAAAAAElFTkSuQmCC',
            }
            let newPollId
            await this.$axios
                .post('/createpoll', poll)
                .then((response) => {
                    console.log(response)
                    newPollId = response.data
                })
                .catch((reason) => {
                    console.log(reason)
                })
            console.log(newPollId)
            this.progressColorA = '#ff4d00'
            await this.$axios
                .post('/addquestion', {
                    pollId: newPollId,
                    questionType: 'ChoiceQuestion',
                    questionMessage: 'Radiobuttons',
                    answerPossibilities: ['1', '2', '3', '4'],
                    numberOfPossibleAnswers: 1,
                    userAnswers: true,
                })
                .then((response) => {
                    console.log(response)
                })
                .catch((reason) => {
                    console.log(reason)
                })
            this.progressColorA = '#ffbb00'
            await this.$axios
                .post('/addquestion', {
                    pollId: newPollId,
                    questionType: 'ChoiceQuestion',
                    questionMessage: 'Checkboxen',
                    answerPossibilities: ['A', 'B', 'C', 'D', 'E'],
                    numberOfPossibleAnswers: 3,
                    userAnswers: true,
                })
                .then((response) => {
                    console.log(response)
                })
                .catch((reason) => {
                    console.log(reason)
                })
            this.progressColorA = '#fbff00'
            await this.$axios
                .post('/addquestion', {
                    pollId: newPollId,
                    questionMessage: 'Textfrage Single Line',
                    questionType: 'TextQuestion',
                    textMultiline: false,
                    textMinimum: 1,
                    textMaximum: 50,
                })
                .then((response) => {
                    console.log(response)
                })
                .catch((reason) => {
                    console.log(reason)
                })
            this.progressColorA = '#aaff00'
            await this.$axios
                .post('/addquestion', {
                    pollId: newPollId,
                    questionMessage: 'Textfrage Multiline',
                    questionType: 'TextQuestion',
                    textMultiline: true,
                    textMinimum: 0,
                    textMaximum: 150,
                })
                .then((response) => {
                    console.log(response)
                })
                .catch((reason) => {
                    console.log(reason)
                })
            this.progressColorA = '#5eff00'
            await this.$axios
                .post('/addquestion', {
                    pollId: newPollId,
                    questionMessage: 'Rangefrage',
                    questionType: 'RangeQuestion',
                    startValue: 20,
                    endValue: 80,
                    stepSize: 10,
                    belowMessage: 'Unter 20',
                    aboveMessage: 'Über 80',
                })
                .then((response) => {
                    console.log(response)
                })
                .catch((reason) => {
                    console.log(reason)
                })
            this.progressColorA = '#00ff84'
            await this.$axios
                .post('/addquestion', {
                    pollId: newPollId,
                    questionMessage: 'Sliderfrage nach Gefühl',
                    questionType: 'SliderQuestion',
                    startValue: 0.0,
                    endValue: 1.0,
                    stepSize: 0.01,
                    belowMessage: 'Wenig',
                    aboveMessage: 'Viel',
                    hideValues: true,
                })
                .then((response) => {
                    console.log(response)
                })
                .catch((reason) => {
                    console.log(reason)
                })
            this.progressColorA = '#00d5ff'
            await this.$axios
                .post('/addquestion', {
                    pollId: newPollId,
                    questionType: 'SliderQuestion',
                    questionMessage: 'Sliderfrage nach Wert',
                    startValue: 0,
                    endValue: 5000,
                    stepSize: 10,
                    belowMessage: 'Unten',
                    aboveMessage: 'Oben',
                    hideValues: false,
                })
                .then((response) => {
                    console.log(response)
                })
                .catch((reason) => {
                    console.log(reason)
                })
            await this.$axios
                .post('/addquestion', {
                    pollId: newPollId,
                    questionType: 'SortQuestion',
                    questionMessage: 'Sortierfrage',
                    answerPossibilities: ['A', 'B', 'C', 'D', 'E'],
                })
                .then((response) => {
                    console.log(response)
                })
                .catch((reason) => {
                    console.log(reason)
                })
            this.progressColorA = '#006eff'
            this.initialize()
        },

        async answerPoll() {
            const polls = this.prepareItems
            const targetPollId = polls[polls.length - 1].pollId
            const response = await this.$axios.get('/getonepoll', {
                params: {
                    pollId: targetPollId,
                },
            })
            const pollData = response.data
            console.log('Beantworte poll: ' + targetPollId)
            console.log(pollData)
            for (let j = 0; j < 25; j++) {
                if (j < 200) {
                    this.progressColorB = '#006eff'
                } else if (j < 175) {
                    this.progressColorB = '#00d5ff'
                } else if (j < 150) {
                    this.progressColorB = '#00ff84'
                } else if (j < 125) {
                    this.progressColorB = '#5eff00'
                } else if (j < 100) {
                    this.progressColorB = '#aaff00'
                } else if (j < 75) {
                    this.progressColorB = '#fbff00'
                } else if (j < 50) {
                    this.progressColorB = '#ff4d00'
                } else if (j < 25) {
                    this.progressColorB = '#ff0000'
                }
                const i = this.participantNr
                this.participantNr += 2
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: targetPollId,
                    questionId: pollData.categoryList[0].questionList[0].questionId,
                    answerList: ['' + Math.floor(Math.random() * 4)],
                })
                console.log(1)
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: targetPollId,
                    questionId: pollData.categoryList[0].questionList[1].questionId,
                    answerList: ['' + Math.floor(Math.random() * 5), '' + Math.floor(Math.random() * 4)],
                })
                console.log(2)
                const p = ['Haus', 'Baum', 'Apfel', 'Spitzzange', 'Webcam']
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: targetPollId,
                    questionId: pollData.categoryList[0].questionList[2].questionId,
                    answerList: [p[Math.floor(Math.random() * 4)]],
                })
                console.log(3)
                const p2 = [
                    'Das ist ein langer Text',
                    'Das ist ein scheiße langer Text',
                    'Das ist auch ein sehr lange Text',
                ]
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: targetPollId,
                    questionId: pollData.categoryList[0].questionList[3].questionId,
                    answerList: [p2[Math.floor(Math.random() * 2)]],
                })
                console.log(4)
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: targetPollId,
                    questionId: pollData.categoryList[0].questionList[4].questionId,
                    answerList: [Math.floor(Math.random() * 8)],
                })
                console.log(5)
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: targetPollId,
                    questionId: pollData.categoryList[0].questionList[5].questionId,
                    answerList: [Math.floor(Math.random() * 100) / 100],
                })
                console.log(6)
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: targetPollId,
                    questionId: pollData.categoryList[0].questionList[6].questionId,
                    answerList: [Math.floor((Math.random() * 50000) / 10)],
                })
                console.log(7)
                const array = ['0', '1', '2', '3', '4']
                let a, b
                for (let k = array.length - 1; k > 0; k--) {
                    a = Math.floor(Math.random() * (k + 1))
                    b = array[k]
                    array[k] = array[a]
                    array[a] = b
                }
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: targetPollId,
                    questionId: pollData.categoryList[0].questionList[7].questionId,
                    answerList: array,
                })
                console.log(8)
            }
        },
    },
}
</script>
