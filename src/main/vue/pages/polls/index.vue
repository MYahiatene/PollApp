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
                                        <v-icon @click="changePollStatus(item)">
                                            {{ item.statusIcon }}
                                        </v-icon>
                                    </template>
                                    <template v-slot:item.action1="{ item }">
                                        <v-icon @click="itemAction1(item)">
                                            {{ item.action1Icon }}
                                        </v-icon>
                                    </template>
                                    <template v-slot:item.action2="{ item }">
                                        <v-icon @click="itemAction2(item)">
                                            {{ item.action2Icon }}
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
            participantNr: 2,
            progressColorA: '#FF0000',
            progressColorB: '#FF0000',
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
                { text: '', value: 'action1', sortable: false },
                { text: '', value: 'action2', sortable: false },
                { text: 'Umfrage', value: 'pollName' },
                { text: 'Erstellt von', value: 'pollCreator' },
                { text: 'Status', value: 'pollStatusString' },
                { text: 'Beantwortet von', value: 'paticipantCount' },
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
        progressA() {
            return this.progressColorA
        },
        progressB() {
            return this.progressColorB
        },
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
                    data[i].action1Icon = 'mdi-pencil'
                    data[i].action2Icon = 'mdi-delete'
                    data[i].statusIcon = 'mdi-play'
                } else if (this.items[i].pollStatus === 1) {
                    data[i].pollStatusString = 'Aktiv'
                    data[i].action1Icon = 'mdi-magnify'
                    data[i].action2Icon = 'mdi-link-variant'
                    data[i].statusIcon = 'mdi-stop'
                } else if (this.items[i].pollStatus === 2) {
                    data[i].pollStatusString = 'Beendet'
                    data[i].action1Icon = 'mdi-magnify'
                    data[i].action2Icon = 'mdi-delete'
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
        ...mapActions({ initialize: 'navigation/initialize', updatePollStatus: 'navigation/updatePollStatus' }),
        ...mapMutations({ setPollActive: 'navigation/setPollActive', setPollFinished: 'navigation/setPollFinished' }),

        async initializeLinks() {
            await this.$axios.get('/participationLinks').then((response) => {
                this.participationLinks = response.data
                console.log(response.data)
            })
        },
        changePollStatus(item) {
            if (item.pollStatus === 0) {
                if (confirm('Umfrage jetzt veröffentlichen?')) {
                    this.updatePollStatus(item.pollId)
                }
            } else if (item.pollStatus === 1) {
                if (confirm('Umfrage jetzt beenden?')) {
                    this.updatePollStatus(item.pollId)
                }
            } else if (confirm('Umfrage kopieren?')) {
                alert('Tja, das geht noch nicht...')
            }
        },
        itemAction1(item) {
            if (item.pollStatus === 0) {
                this.$router.push('/polls/' + item.pollId)
            } else {
                this.$router.push('/eval/' + item.pollId)
            }
        },
        itemAction2(item) {
            if (item.pollStatus === 1) {
                this.setLink(item)
            } else if (confirm('Umfrage entgültig löschen?')) {
                this.deletePoll(item)
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
                    /* alert( //TODO: extrem nervig beim Testen
                        'Link kopiert: "localhost:8080/participant/' +
                            this.participationLinks[i].participationLink +
                            '"'
                    ) */
                }
            }
        },
        deletePoll(item) {
            alert('Jetzt wäre die Umfrage gelöscht...')
        },
        async initializeDatabase() {
            const today = new Date()
            const dd = String(today.getDate()).padStart(2, '0')
            const mm = String(today.getMonth() + 1).padStart(2, '0') // January is 0!
            const yyyy = today.getFullYear()
            const poll = {
                pollcreator: 'Jan',
                anonymityStatus: '1',
                pollname: 'Beispielumfrage',
                pollCreatedAt: dd + '.' + mm + '.' + yyyy,
                activatedAt: dd + '.' + mm + '.' + yyyy,
                deactivatedAt: dd + '.' + mm + '.' + yyyy,
                pollStatus: 0,
                categoryChange: true,
                visibility: true,
                backgroundColor: '#555555',
                fontColor: '#fe7312',
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
            this.progressColorA = '#006eff'
            this.initialize()
            await this.initializeLinks()
        },

        async answerPoll() {
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
                    pollId: 1,
                    questionId: 4,
                    answerList: [Math.floor(Math.random() * 4)],
                })
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: 1,
                    questionId: 5,
                    answerList: [Math.floor(Math.random() * 5), Math.floor(Math.random() * 4)],
                })
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: 1,
                    questionId: 6,
                    answerList: ['Hallo'],
                })
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: 1,
                    questionId: 7,
                    answerList: ['Das ist ein langer Text'],
                })
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: 1,
                    questionId: 8,
                    answerList: [Math.floor(Math.random() * 8)],
                })
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: 1,
                    questionId: 9,
                    answerList: [Math.floor(Math.random() * 100) / 100],
                })
                await this.$store.dispatch('participant/saveAnswer', {
                    username: '' + i,
                    pollId: 1,
                    questionId: 10,
                    answerList: [Math.floor((Math.random() * 50000) / 10)],
                })
            }
        },
    },
}
</script>
