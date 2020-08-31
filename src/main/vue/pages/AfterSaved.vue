<template>
    <v-layout column justify-center align-center>
        <v-flex xs12 sm8 md6>
            <div class="text-center"></div>
            <v-card>
                <v-card-title class="headline"> Ihre Antworten wurden gespeichert! </v-card-title>
                <v-card-text>
                    Wenn Sie einen persönlichen Link erhalten haben können Sie Ihre Umfrage bis zum Deaktivierungsdatum
                    noch verändern.
                </v-card-text>
                <v-card-actions>
                    <v-spacer />
                    <v-btn color="primary" nuxt to="/"> Start </v-btn>
                </v-card-actions>
            </v-card>
        </v-flex>
    </v-layout>
</template>
