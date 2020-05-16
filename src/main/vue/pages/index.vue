<template>
    <v-layout column justify-center align-center>
        <v-flex xs12 sm8 md6>
            <div class="text-center"></div>
            <v-card>
                <v-card-title class="headline">
                    Willkommen zu Umfrato!
                </v-card-title>
                <v-img src="Logo.svg"></v-img>
                <v-card-text>
                    Viel Spaß beim Umfragen erstellen!
                </v-card-text>
                <v-card-actions>
                    <v-spacer />
                    <v-btn color="primary" nuxt to="/login">
                        Login
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-flex>
    </v-layout>
</template>
