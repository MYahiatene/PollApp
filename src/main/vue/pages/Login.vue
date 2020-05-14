<template>
    <v-card width="400" class="mx-auto mt-5">
        <v-card-title>
            <h1 class="display-1">Login</h1>
        </v-card-title>
        <v-card-text>
            <v-form>
                <v-text-field label="Username" prepend-icon="mdi-account-circle" />
                <v-text-field
                    :type="showPassword ? 'text' : 'password'"
                    label="Password"
                    prepend-icon="mdi-lock"
                    :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                    @click:append="showPassword = !showPassword"
                />
            </v-form>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="info">Login</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
import axios from 'axios'
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
    login(username, password) {
        const credentials = new URLSearchParams()
        credentials.append('username', username)
        credentials.append('password', password)
        return axios.post('/api/authenticate', credentials)
    },
    computed: {
        ...mapGetters({
            authenticated: 'isAuthenticated',
        }),
    },
    methods: {
        ...mapActions(['requestToken']),
        login() {
            this.requestToken(this.auth).then(() => this.$router.push('/'))
        },
    },
    watch: {
        count(newCount, oldCount) {
            // Our fancy notification (2).
            console.log(`We have ${newCount} fruits now, yay!`)
        },
    },
}
</script>

<style scoped></style>
