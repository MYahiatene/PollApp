<template>
    <v-app style="background: #eefcfa;">
        <v-app-bar fixed app>
            <nuxt-link to="/" style="text-decoration: none;">
                <div>
                    <h1><img src="LogoEinfach.svg" alt="Logo" width="25" /> {{ title }}</h1>
                </div>
            </nuxt-link>

            <v-spacer></v-spacer>

            <v-div v-for="(item, i) in items" :key="i" router exact>
                <v-btn class="ma-3" :to="item.to" color="primary" :disabled="isAuthenticated !== true">
                    {{ item.title }}
                </v-btn>
            </v-div>
            <div v-if="isAuthenticated">
                <v-btn text :to="'/Login'"> <v-icon>mdi-account</v-icon> {{ getUsername }} </v-btn>
            </div>
            <div v-else>
                <v-btn text :to="'/Login'"> <v-icon>mdi-account</v-icon> Login </v-btn>
            </div>
        </v-app-bar>

        <v-content>
            <v-container>
                <nuxt />
            </v-container>
        </v-content>

        <v-footer :fixed="fixed" app>
            <span>&copy; {{ new Date().getFullYear() }}</span>
        </v-footer>
    </v-app>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
    data() {
        return {
            fixed: false,
            items: [
                {
                    title: 'Umfragen',
                    to: '/navigation',
                },
                {
                    title: 'Auswertung',
                    to: '/Analyse',
                },
                {
                    title: 'Administration',
                    to: '/Administration',
                },
            ],
            title: 'Umfrato',
        }
    },
    computed: {
        ...mapGetters({
            isAuthenticated: 'login/isAuthenticated',
            getUsername: 'login/getUsername',
        }),
    },
}
</script>
