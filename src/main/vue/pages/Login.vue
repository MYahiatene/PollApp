<template>
    <b-container>
        <b-row>
            <b-col cols="6" offset="3" class="mt-3">
                <b-card header="Login">
                    <b-form @submit.prevent="login">
                        <b-form-group label="Username:">
                            <b-form-input v-model="auth.username" type="text"></b-form-input>
                        </b-form-group>
                        <b-form-group label="Passwort:">
                            <b-form-input v-model="auth.password" type="password"></b-form-input>
                        </b-form-group>
                        <b-button type="submit" variant="primary">Login</b-button>
                    </b-form>
                    <b-alert variant="danger" :show="authenticated === false">
                        Bitte überprüfen Sie Ihre Angaben.
                    </b-alert>
                </b-card>
            </b-col>
        </b-row>
    </b-container>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

export default {
    name: 'Login',
    data() {
        return {
            auth: {
                username: '',
                password: '',
            },
        }
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
}
</script>

<style scoped></style>
