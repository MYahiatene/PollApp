<template>
    <div v-if="getPoll[1] !== undefined" @change="fontColor">
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container>
            <div v-if="getPoll[1].data.logo !== undefined">
                <img :src="getPoll[1].data.logo" alt="failedToLoadLogo" />
            </div>
            <v-subheader class="display-1 font-weight-bold" :style="fontColorText"> Umfrage 1</v-subheader>
            <v-content>
                <v-row>
                    <v-col cols="8">
                        <!--v-list v-for="poll in getPoll.data" :key="poll.id"-->
                        <v-list v-for="category in getPoll[1].data.categoryList" :key="category.categoryId" two-line>
                            <v-list v-for="question in category.questionList" :key="question.questionId" two-line>
                                <v-card class="mx-auto" tile>
                                    <v-card-title class="col" :style="fontColorText">
                                        <!--the visibility of the number of questions depends on the poll configuration-->
                                        <!--div v-if="getVisibility">{{ question.id }}/{{ questions.length }}</div-->
                                        {{ question.questionMessage }}
                                    </v-card-title>
                                    <div v-if="question.questionType === 'textfield'">
                                        <v-text-field label="Antwort" :color="fontColor"> </v-text-field>
                                    </div>
                                    <div v-else-if="question.questionType === 'choicebox'">
                                        <v-list v-for="answer in question.answerPossibilities" :key="answer.text">
                                            <v-checkbox
                                                class="ma-4 red--text"
                                                :label="answer"
                                                :color="fontColor"
                                            ></v-checkbox>
                                        </v-list>
                                        <!--div v-if="question.ownAnswersAllowed">
                                                <v-checkbox
                                                    v-model="enabled"
                                                    hide-details
                                                    class="ma-4"
                                                    label="Eigene Antwort:"
                                                ></v-checkbox>
                                                <v-col>
                                                    <v-text-field></v-text-field>
                                                </v-col>
                                            </div-->
                                    </div>
                                </v-card>
                            </v-list></v-list
                        >
                        <!--/v-list-->
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="8">
                        <v-btn class="pl-4">Vorherige Seite</v-btn>
                        <v-btn class="pl-4">Nächste Seite</v-btn>
                        <v-btn color="primary" nuxt to="/AfterParticipated">
                            Absenden
                        </v-btn>
                    </v-col>
                </v-row>
            </v-content>
        </v-container>
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
import AuthGate from '../components/AuthGate'

export default {
    name: 'Participant',
    layout: 'participant',
    components: { AuthGate },
    data: () => ({
        enabled: false,
        //     url:
        //         'https://lime-imagecache.xingassets.com/public-imagecache_production/images/s3_attachments/152/517/924/original/d722f0c1-8a92-4ba4-8dfe-4faae23c1b49.jpg?X-Amz-Expires=604800&X-Amz-Date=20200524T173544Z&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=lime-imagecache_production/20200524/xing/s3/aws4_request&X-Amz-SignedHeaders=host&X-Amz-Signature=dcf5b13f553a59d0a4b3996d892dc8474ef014ccd92c0c8c8e0aaedd360838df',
    }),
    created() {
        this.showPoll()
    },
    computed: {
        ...mapGetters({
            getPoll: 'participant/getPoll',
            isAuthenticated: 'login/isAuthenticated',
        }),
        fontColor() {
            return this.getPoll[1].data.fontColor
        },
        fontColorText() {
            return 'color:' + this.getPoll[1].data.fontColor
        },
    },
    methods: {
        showPoll() {
            this.$store.dispatch('participant/showPoll')
        },
        // ...mapActions({ showPoll: 'participant/showPoll' }),
        // fontColor() {
        //     this.primary = this.getPoll[1].data.fontColor
        // },
    },
}
</script>

<style scoped></style>
