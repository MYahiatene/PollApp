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
                    @keydown.enter="requestToken"
                />
            </v-form>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="secondary" @click="requestToken">Login</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
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
    mounted() {
        this.autoLogin()
        this.setRoleInStore()
    },
    computed: {
        ...mapGetters({
            authenticated: 'login/isAuthenticated',
        }),
    },
    methods: {
        ...mapActions({ requestToken: 'login/requestToken', autoLogin: 'login/autoLogin' }),
        ...mapMutations({ setRole: 'account/setRole' }),
        setRoleInStore() {
            let role = ''
            const errorString = []
            this.$axios
                .get('/checkToken')
                .then((response) => {
                    if (response.status === 200) {
                        role = 'Admin'
                        this.setRole(role)
                    }
                })
                .catch((error) => {
                    errorString.push(error)
                })
            this.$axios
                .get('/checkCreatorToken')
                .then((response) => {
                    if (response.status === 200) {
                        role = 'Creator'
                        this.setRole(role)
                    }
                })
                .catch((error) => {
                    errorString.push(error)
                })
            this.$axios
                .get('/checkEditorToken')
                .then((response) => {
                    if (response.status === 200) {
                        role = 'Editor'
                        this.setRole(role)
                    }
                })
                .catch((error) => {
                    errorString.push(error)
                })
            if (errorString.length > 2) {
                this.setRole('User')
            }
        },
        async requestToken() {
            await this.$store.dispatch('login/requestToken', this.auth).catch((reason) => {
                // console.log(reason)
            })
            if (this.authenticated) {
                await this.$router.push('/')
                await this.setRoleInStore()
            }
        },
    },
}
</script>

<style scoped></style>
