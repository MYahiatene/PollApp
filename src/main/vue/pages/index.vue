<template>
    <v-container>
        <v-layout column justify-center align-center>
            <v-flex xs12 sm8 md6>
                <!--                        <div class="text-center"></div>-->

                <v-row>
                    <v-col cols="12" lg="12">
                        <v-card align="center">
                            <v-card-title class="headline">
                                Willkommen bei Umfrato!
                            </v-card-title>
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
                                                    <p class="pa-5">Es gibt noch nichts anzuzeigen.</p>
                                                    <v-list subheader cols="12" lg="5">
                                                        <v-list-item
                                                            v-for="item in umfragen"
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
                                                                <v-btn icon>
                                                                    <v-icon color="secondary">
                                                                        {{ item.iconStatus }}
                                                                    </v-icon>
                                                                </v-btn>
                                                            </v-list-item-action>
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
                                        <div v-else>
                                            Bitte melden Sie sich an.
                                        </div>
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
                        <v-card class="pa-2" style="background-color: #ffffff;">
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
            umfragen: [
                /* {
                    title: 'Meinungsbild Teambuilding',
                    id: '418',
                    subtitle: 'Fragen: 3 Bearbeiter: 1 Zuletzt: NHILLE (14.05.2020)',
                    iconAction: 'mdi-pencil',
                    actionLink: '/polls/1',
                    iconStatus: 'mdi-play',
                },
                {
                    title: 'Wahl der Vertrauensperson',
                    id: '215',
                    subtitle: 'Teilgenommen: 52% (18/34) Tage übrig: 5 (26.05.2020)',
                    iconAction: 'mdi-magnify',
                    actionLink: '/eval/1',
                    iconStatus: 'mdi-stop',
                },
                {
                    title: 'Weihnachtsfeier 2019 Rückblick',
                    id: '183',
                    subtitle: 'Automatisch geschlossen seit 01.02.2020',
                    iconAction: 'mdi-magnify',
                    actionLink: '/eval/1',
                    iconStatus: 'mdi-content-duplicate',
                }, */
            ],
        }
    },
    computed: {
        ...mapGetters({
            isAuthenticated: 'login/isAuthenticated',
        }),
    },
}
</script>
