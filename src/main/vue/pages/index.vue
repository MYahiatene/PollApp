<template>
    <v-layout column justify-center align-center>
        <v-flex xs12 sm8 md6>
            <div class="text-center">
                <vuetify-logo />
            </div>
            <v-card>
                <v-card-title class="headline">
                    Welcome to the Vuetify + Nuxt.js template
                </v-card-title>
            </v-card>
        </v-flex>
    </v-layout>
</template>

<script>
import VuetifyLogo from '~/components/VuetifyLogo.vue'

export default {
    components: {
        VuetifyLogo,
    },
}
</script>
