<template>
    <div>
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-layout v-else>
            <v-flex class="text-center">
                <v-card class="pa-5">
                    <h1>Administrationsseite</h1>

                    <v-btn color="primary">
                        primary
                    </v-btn>

                    <v-btn color="secondary">
                        secondary
                    </v-btn>

                    <v-btn color="info">
                        info
                    </v-btn>

                    <v-btn color="accent">
                        accent
                    </v-btn>

                    <v-btn color="success">
                        success
                    </v-btn>

                    <v-btn color="warning">
                        warning
                    </v-btn>

                    <v-btn color="error">
                        error
                    </v-btn>

                    <v-spacer />
                </v-card>
            </v-flex>
        </v-layout>
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
import AuthGate from '../components/AuthGate'
export default {
    components: { AuthGate },
    computed: {
        ...mapGetters({
            isAuthenticated: 'login/isAuthenticated',
        }),
    },
}
</script>
