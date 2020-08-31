<template>
    <v-layout column justify-center align-center>
        <v-flex xs12 sm8 md6>
            <div class="text-center"></div>
            <v-card>
                <v-card-title class="headline"> Vielen Dank für Ihre Teilnahme! </v-card-title>
                <v-card-text> Bitte stimmen Sie nur einmal ab. </v-card-text>
                <v-card-actions>
                    <v-spacer />
                    <v-btn color="primary" nuxt to="/"> Start </v-btn>
                </v-card-actions>
            </v-card>
        </v-flex>
    </v-layout>
</template>
