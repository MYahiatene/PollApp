<template>
    <v-card width="400" class="mx-auto mt-5">
        <v-card-title>
            <h1 class="display-1">Login</h1>
        </v-card-title>
        <v-card-text>
            <v-form>
                <v-text-field v-model="auth.username" label="Username" prepend-icon="mdi-account-circle" />
                <v-text-field
                    v-model="auth.password"
                    :type="showPassword ? 'text' : 'password'"
                    label="Password"
                    prepend-icon="mdi-lock"
                    :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                    :error="authenticated === false"
                    @click:append="showPassword = !showPassword"
                />
            </v-form>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="info" @click="requestToken">Login</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
// import axios from 'axios'
import { mapActions, mapGetters } from 'vuex'
export default {
    name: 'Login',
    data() {
        return {
            auth: {
                username: '',
                password: '',
            },
            showPassword: false,
        }
    },
    computed: {
        ...mapGetters({
            authenticated: 'login/isAuthenticated',
        }),
    },
    methods: {
        ...mapActions({ requestToken: 'login/requestToken' }),
        requestToken() {
            this.$store.dispatch('login/requestToken', this.auth)
        },
    },
    watch: {
        authenticated: () => {
            alert('Willkommen')
        },
    },
}
</script>

<style scoped></style>
