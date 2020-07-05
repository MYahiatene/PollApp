<template>
    <div>
        <div>
            <v-container class="grey lighten-5">
                <v-card-title>Erinnerung an die Teilnehmer der Umfrage schicken</v-card-title>
                <v-container fluid>
                    <v-row>
                        <v-col cols="12" md="6">
                            <v-sheet elevation="12" class="pa-12">
                                <v-textarea
                                    v-model="mailSubject"
                                    :auto-grow="autoGrow"
                                    :clearable="clearable"
                                    :counter="counter ? counter : false"
                                    :filled="filled"
                                    :flat="flat"
                                    :label="labelSubject"
                                    :loading="loading"
                                    :no-resize="noResize"
                                    :outlined="outlined"
                                    :placeholder="placeholderSubject"
                                    :rounded="rounded"
                                    :row-height="rowHeightSubject"
                                    :rows="rowsSubject"
                                    :shaped="shaped"
                                    :single-line="singleLine"
                                    :solo="solo"
                                ></v-textarea>
                                <v-textarea
                                    v-model="mailText"
                                    :auto-grow="autoGrow"
                                    :clearable="clearable"
                                    :counter="counter ? counter : false"
                                    :filled="filled"
                                    :flat="flat"
                                    :hint="hint"
                                    :label="label"
                                    :loading="loading"
                                    :no-resize="noResize"
                                    :outlined="outlined"
                                    :persistent-hint="persistentHint"
                                    :placeholder="placeholder"
                                    :rounded="rounded"
                                    :row-height="rowHeight"
                                    :rows="rows"
                                    :shaped="shaped"
                                    :single-line="singleLine"
                                    :solo="solo"
                                ></v-textarea>
                                <v-row justify="end">
                                    <v-btn color="primary" dark class="mb-2" justify="end" @click="sendRemindEmail"
                                        >E-Mail senden</v-btn
                                    >
                                </v-row>
                            </v-sheet>
                        </v-col>
                    </v-row>
                </v-container>
            </v-container>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            file: null,
            items: [],
            message: '',
            poll: [],

            autoGrow: true,
            autofocus: true,
            clearable: true,
            counter: 0,
            filled: true,
            flat: false,
            hint: 'Um den Teilnahmelink hinzuzufügen, fügen SIe im Text "{link}" ein.',
            label: 'E-Mail Nachricht',
            loading: false,
            mailText:
                'Hallo Teilnehmer!\n\nHier ist eine Erinnerung an deine Umfrage. Bitte fülle sie bald noch aus!\n' +
                '{link}\nViel Spaß bei der Teilnahme!\n\n' +
                'Mit freundlichen Grüßen\n' +
                'Dein Umrato Team',
            noResize: false,
            outlined: false,
            persistentHint: true,
            placeholder: 'Geben Sie hier Ihre E-Mail Nachricht ein, die an den Teilnehmer gesendet werden soll.',
            rounded: false,
            rowHeight: 24,
            rows: 10,
            shaped: false,
            singleLine: false,
            solo: false,
            labelSubject: 'E-Mail Betreff',
            mailSubject: 'Erinnerung an die Umfrage',
            placeholderSubject: 'Geben Sie hier den E-Mail Betreff an.',
            rowsSubject: '1',
            rowHeightSubject: '1',
        }
    },
    created() {
        this.loadPoll()
    },
    copmuted: {
        pollId() {
            return parseInt(this.$route.params.MailReminding)
        },
    },
    methods: {
        async loadPoll() {
            await this.$axios
                .get('/getonepoll', {
                    params: {
                        pollId: this.$route.params.MailReminding,
                    },
                })
                .then((response) => {
                    this.poll = response.data
                    console.log(this.poll)
                })
                .catch((error) => {
                    console.log(error)
                })
        },
        sendRemindEmail() {
            console.log('Email Sending test')
            console.log(this.items)
            const obj = {
                emailSubject: this.mailSubject,
                emailMessage: this.mailText,
                pollId: this.poll.pollId,
            }
            this.$axios
                .post('/sendRemindEmail', obj)
                .then(function () {
                    console.log('Email sending succedeed.')
                })
                .catch((error) => console.log(error))
            this.$router.push('/polls')
        },
    },
}
</script>

<style scoped></style>
