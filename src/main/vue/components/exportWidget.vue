<template>
    <div class="text-center">
        <v-menu top>
            <template v-slot:activator="{ on, attrs }">
                <v-btn color="primary" dark v-bind="attrs" v-on="on">
                    Export
                </v-btn>
            </template>
            <v-row>
                <v-col>
                    <v-card>
                        <v-app-bar dense flat>
                            <v-toolbar-title> Export von Umfragedaten </v-toolbar-title>
                            <v-spacer></v-spacer>
                        </v-app-bar>

                        <v-card-text
                            ><v-list>
                                <v-list-item>
                                    <v-btn class="pl-4" @click="exportAnswers()"
                                        ><v-icon>mdi-export</v-icon>Poll exportieren
                                    </v-btn>
                                    <v-list-item-title>{{ Title }}</v-list-item-title>
                                </v-list-item>
                                <v-list-item>
                                    <v-btn class="pl-4" @click="exportResults()"
                                        ><v-icon>mdi-export</v-icon>PollResults exportieren
                                    </v-btn>
                                    <v-list-item-title>{{ Title }}</v-list-item-title>
                                </v-list-item>
                                <v-list-item>
                                    <v-btn class="pl-4" @click="exportCSV()"
                                        ><v-icon>mdi-magnify</v-icon>Poll menschenleserlich exportieren
                                    </v-btn>
                                    <v-list-item-title>{{ Title }}</v-list-item-title>
                                </v-list-item>
                            </v-list></v-card-text
                        >
                    </v-card>
                </v-col>
            </v-row>
        </v-menu>
    </div>
    <!--<v-container>
            <v-btn class="pl-4" @click="exportAnswers()"><v-icon>mdi-export</v-icon>Poll exportieren </v-btn>
            <v-btn class="pl-4" @click="exportResults()"><v-icon>mdi-export</v-icon>PollResults exportieren </v-btn>
            <v-btn class="pl-4" @click="exportCSV()"
                ><v-icon>mdi-magnify</v-icon>Poll menschenleserlich exportieren
            </v-btn>
            <v-col cols="12" md="4">
                Poll importieren
                <input type="file" @change="uploadFile" />
            </v-col>
        </v-container>-->
</template>

<script>
import { mapActions } from 'vuex'

export default {
    name: 'ExportWidget',
    methods: {
        ...mapActions({ initialize: 'evaluation/initialize', updateData: 'evaluation/updateData' }),

        exportAnswers() {
            this.$store.dispatch('evaluation/exportAnswers', 1) // This should be PollId
            this.downloadClick(1)
        },

        exportResults() {
            this.$store.dispatch('evaluation/exportResults', 1) // This should be PollId
            this.downloadClickResult(1)
        },

        exportCSV() {
            this.$store.dispatch('evaluation/exportCSV', 1) // This should be PollId
            this.downloadClickCSV(1)
        },

        downloadClick(pollId) {
            console.log(this.$store.dispatch('evaluation/awaitPollText', pollId))
        },

        downloadClickResult(pollId) {
            console.log(this.$store.dispatch('evaluation/awaitPollResultText', pollId))
        },

        downloadClickCSV(pollId) {
            console.log(this.$store.dispatch('evaluation/awaitPollResultCsv', pollId))
        },

        uploadFile(e) {
            const file = e.target.files[0]
            const reader = new FileReader()
            reader.readAsText(file)
            reader.onload = (e) => {
                console.log('File: ', file)
                console.log('FileE: ', e.target.result)
                console.log(this.$store.dispatch('evaluation/importPoll', e.target.result))
            }
        },
    },
}
</script>

<style scoped></style>
