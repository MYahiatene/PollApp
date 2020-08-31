<template>
    <v-container>
        <v-layout column justify-center align-center>
            <v-layout xs12 sm8 md6>
                <v-row>
                    <v-col cols="12" lg="12">
                        <v-row>
                            <v-card>
                                <v-card-title class="headline"> Willkommen bei Umfrato! </v-card-title>
                                <v-card-text>
                                    <v-container>
                                        <v-row>
                                            Bitte melden Sie sich an.
                                            <v-spacer />
                                            <v-btn color="accent" nuxt to="/login"> Login </v-btn>
                                        </v-row>
                                    </v-container>
                                </v-card-text>
                            </v-card>
                        </v-row>
                    </v-col>
                    <br />
                    <v-col cols="12" lg="3"></v-col>
                </v-row>
            </v-layout>
        </v-layout>
    </v-container>
</template>

<script>
export default {
    name: 'AuthGate',
}
</script>

<style scoped></style>
