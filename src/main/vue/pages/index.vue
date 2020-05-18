<template>
    <v-container>
        <v-layout column justify-center align-center>
            <v-flex xs12 sm8 md6>
                <!--                        <div class="text-center"></div>-->

                <v-row>
                    <v-col cols="12" lg="12">
                        <row>
                            <v-card hover>
                                <v-card-title class="headline">
                                    Willkommen bei Umfrato!
                                </v-card-title>

                                <v-card-text>
                                    <v-container>
                                        <v-row>
                                            Viel Spaß beim Umfragen erstellen!
                                            <v-spacer />
                                            <v-btn v-if="isAuthenticated !== true" color="accent" nuxt to="/login">
                                                Login
                                            </v-btn>
                                        </v-row>
                                    </v-container>
                                </v-card-text>
                            </v-card>
                        </row>
                    </v-col>
                    <br />
                    <v-col cols="12" lg="3"></v-col>
                    <v-col cols="12" lg="6" align="center" justify="center">
                        <v-card class="pa-2" style="background-color: #ffffff;">
                            <v-img src="LogoMitMehr.svg"></v-img>
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
    name: 'index',
    computed: {
        ...mapGetters({
            isAuthenticated: 'login/isAuthenticated',
        }),
    },
}
</script>
