<template>
    <v-container>
        <v-layout column justify-center align-center>
            <v-flex xs12 sm8 md6>
                <!--                        <div class="text-center"></div>-->

                <v-row>
                    <v-col cols="12" lg="12">
                        <v-card align="center">
                            <v-card-title class="headline"> Willkommen bei Umfrato! </v-card-title>
                            <v-card-text>
                                <v-container>
                                    <v-row>
                                        <!--Is shown after Loged In-->
                                        <v-col v-if="isAuthenticated" cols="12" lg="3"></v-col>
                                        <v-col v-if="isAuthenticated" cols="12" lg="6">
                                            <div>
                                                <v-card>
                                                    <v-toolbar>
                                                        <v-toolbar-title>Zuletzt angesehen: </v-toolbar-title>
                                                        <v-spacer></v-spacer>
                                                    </v-toolbar>
                                                    <p v-if="polls.length === 0" class="pa-5">
                                                        Es gibt noch nichts anzuzeigen.
                                                    </p>
                                                    <v-list subheader cols="12" lg="5">
                                                        <v-list-item
                                                            v-for="item in polls"
                                                            :key="item.id"
                                                            link
                                                            align="left"
                                                        >
                                                            <v-list-item-content>
                                                                <v-list-item-title
                                                                    v-text="item.title"
                                                                ></v-list-item-title>
                                                                <v-list-item-subtitle
                                                                    v-text="item.subtitle"
                                                                ></v-list-item-subtitle>
                                                            </v-list-item-content>
                                                            <v-list-item-action>
                                                                <v-btn icon nuxt :to="item.actionLink">
                                                                    <v-icon color="primary">
                                                                        {{ item.iconAction }}
                                                                    </v-icon>
                                                                </v-btn>
                                                            </v-list-item-action>
                                                        </v-list-item>
                                                    </v-list>
                                                </v-card>
                                            </div>
                                        </v-col>
                                        <!--Is shown before Loged In-->
                                        <div v-else>Bitte melden Sie sich an.</div>
                                        <v-spacer />
                                        <v-btn v-if="isAuthenticated !== true" color="accent" nuxt to="/login">
                                            Login
                                        </v-btn>
                                    </v-row>
                                </v-container>
                            </v-card-text>
                        </v-card>
                    </v-col>
                    <br />
                    <v-col cols="12" lg="3"></v-col>
                    <v-col cols="12" lg="6" align="center" justify="center">
                        <v-card class="pa-2" style="background-color: #ffffff">
                            <v-img src="LogoMitMehr2.png"></v-img>
                        </v-card>
                    </v-col>
                    <v-col cols="12" lg="3"></v-col>
                </v-row>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
    name: 'Index',
    data() {
        return {
            polls: [],
        }
    },
    mounted() {
        this.getPolls()
    },
    computed: {
        ...mapGetters({
            isAuthenticated: 'login/isAuthenticated',
        }),
    },
    methods: {
        async getPolls() {
            if (this.isAuthenticated) {
                let pollData = []
                await this.$axios
                    .get('/relevantpolls')
                    .then((response) => {
                        // console.log(response)
                        pollData = response.data
                    })
                    .catch((reason) => {
                        // console.log(reason)
                    })
                const data = []
                for (let i = 0; i < pollData.length; i++) {
                    if (pollData[i].pollStatus === 0) {
                        data.push({
                            title: pollData[i].pollName,
                            id: pollData[i].pollId,
                            subtitle: pollData[i].computedSubtitle,
                            iconAction: 'mdi-pencil',
                            actionLink: '/polls/' + pollData[i].pollId,
                        })
                    } else if (pollData[i].pollStatus === 1) {
                        data.push({
                            title: pollData[i].pollName,
                            id: pollData[i].pollId,
                            subtitle: pollData[i].computedSubtitle,
                            iconAction: 'mdi-play',
                            actionLink: '/polls',
                        })
                    } else if (pollData[i].pollStatus === 2) {
                        data.push({
                            title: pollData[i].pollName,
                            id: pollData[i].pollId,
                            subtitle: pollData[i].computedSubtitle,
                            iconAction: 'mdi-magnify',
                            actionLink: '/eval/' + pollData[i].pollId,
                        })
                    } else if (pollData[i].pollStatus === 3) {
                        data.push({
                            title: pollData[i].pollName,
                            id: pollData[i].pollId,
                            subtitle: pollData[i].computedSubtitle,
                            iconAction: 'mdi-magnify',
                            actionLink: '/eval/' + pollData[i].pollId,
                        })
                    }
                }
                this.polls = data
            } else {
                this.polls = []
            }
        },
    },
}
</script>
