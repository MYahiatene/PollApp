<template>
    <v-card class="pa-3">
        <v-card-title> {{ title }} exportieren </v-card-title>
        <v-card-text v-if="exportResult"> Hier exportieren Sie die Ergebnisse einer Umfrage </v-card-text>
        <v-card-text v-else> Hier exportieren Sie eine Umfrage </v-card-text>
        Format:<v-overflow-btn v-model="selectedFormat" label="Exportieren als" :items="formats" />
        <div v-if="selectedFormat === 'csv'">
            <v-switch v-model="useCustomSeparator" label="Benutzerdefiniertes Trennzeichen benutzen" />
            <v-text-field
                v-if="useCustomSeparator"
                v-model="customSeparator"
                label="Trennzeichen"
                :rules="separatorRule"
            />
            <v-switch v-if="exportResult" v-model="dereferenceAnswers" label="Antworten dereferenzieren" />
        </div>
        <div v-if="exportResult">
            <v-switch v-model="useFilters" label="Daten vor Export filtern" />
            <v-overflow-btn v-if="useFilters" v-model="selectedSession" label="Filterquelle" :items="sessions" />
        </div>
        <v-text-field v-model="newFilename" :suffix="(selectedFormat ? '.' : '') + selectedFormat" label="Dateiname" />
        <v-btn
            class="pl-4"
            color="accent"
            :disabled="newFilename.length < 3 || !selectedFormat || customSeparator.length !== 1"
            @click="startExport()"
            >Exportieren</v-btn
        >
    </v-card>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

export default {
    name: 'ExportWidget',
    props: {
        exportResult: {
            type: Boolean,
            default: false,
        },
        exportPollId: {
            type: Number,
            default: -1,
        },
    },
    data() {
        return {
            formats: ['csv', 'json'],
            useFilters: false,
            useCustomSeparator: false,
            selectedSession: '',
            selectedFormat: '',
            newFilename: '',
            fileNumber: -1,
            customSeparator: ';',
            dereferenceAnswers: false,
            separatorRule: [
                (v) => {
                    return v.length !== 1 ? 'Trennzeichen muss genau ein Zeichen sein' : true
                },
            ],
        }
    },
    watch: {
        exportPollId() {
            // console.log('Hi' + this.exportPollId)
        },
    },
    computed: {
        ...mapGetters({
            getSessions: 'evaluation/getSessions',
            currentSession: 'evaluation/getCurrentSession',
            evalPollId: 'evaluation/getPollId',
            poll: 'evaluation/getCurrentPoll',
            smallPoll: 'navigation/getPoll',
        }),

        title() {
            if (this.exportResult) {
                return 'Ergebnisse von ' + this.poll.pollName
            } else {
                if (this.smallPoll) {
                    return this.smallPoll.pollName
                }
                return ''
            }
        },

        pollId() {
            if (this.exportResult) {
                return this.evalPollId
            } else {
                if (this.smallPoll) {
                    return this.smallPoll.pollId
                }
                return -1
            }
        },

        sessionId() {
            if (this.selectedSession === 'Aktuelle Session') {
                return this.currentSession
            } else {
                const sessions = this.getSessions
                for (let i = 0; i < sessions.length; i++) {
                    if (sessions[i].sessionTitle === this.selectedSession) {
                        return sessions[i].sessionId
                    }
                }
            }
            return -1
        },

        sessions() {
            const sessions = ['Aktuelle Session']
            const data = this.getSessions
            for (let i = 0; i < data.length; i++) {
                sessions.push(data[i].sessionTitle)
            }
            return sessions
        },
    },

    mounted() {
        this.newFilename = this.title
    },
    methods: {
        ...mapActions({
            initialize: 'evaluation/initialize',
            updateData: 'evaluation/updateData',
        }),

        async startExport() {
            if (this.exportResult) {
                this.fileNumber = await this.$store.dispatch('evaluation/exportResults', {
                    pollId: this.pollId,
                    sessionID: this.useFilters ? this.sessionId : -2,
                    formatString: this.selectedFormat,
                    customSeparator: this.customSeparator,
                    dereference: this.dereferenceAnswers,
                })
                this.downloadClick()
            } else {
                this.fileNumber = await this.$store.dispatch('evaluation/exportPoll', {
                    pollId: this.pollId,
                    formatString: this.selectedFormat,
                    customSeparator: this.customSeparator,
                })
                this.downloadClick()
            }
        },

        downloadClick() {
            if (
                this.$store.dispatch('evaluation/awaitFile', {
                    fileName: this.newFilename,
                    fileNumber: this.fileNumber,
                    fileFormat: this.selectedFormat,
                })
            ) {
                this.$emit('done')
            }
        },
    },
}
</script>

<style scoped></style>
