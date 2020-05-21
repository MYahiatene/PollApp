<template>
    <div>
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-layout v-else>
            <v-flex class="text-center">
                <h1>Umfrageseite</h1>
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
