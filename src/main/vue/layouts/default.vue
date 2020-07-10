<template>
    <!--     In this file we define the header that will be displayed on all pages in the entire app -->
    <v-app :dark="setTheme === 'true'" :style="backgroundColor">
        <!--        set Theme returns a boolean value depending on the theme the user wants (light=false, dark=true)-->
        <!--        backgroundColor is a computed string that sets the background to the color defined in the nuxt.config.js depending on the chosen theme-->
        <v-app-bar fixed app>
            <!--            Logo and Name of the App-->
            <nuxt-link to="/" style="text-decoration: none;">
                <div>
                    <h1><img src="LogoEinfach.svg" alt="Logo" width="25" /> {{ title }}</h1>
                </div>
            </nuxt-link>

            <v-spacer></v-spacer>
            <!--            Umfrage Button-->
            <div v-if="isAuthenticated">
                <v-btn class="ma-3" :to="'/polls'" color="primary" :disabled="isEditor === false">
                    Umfragen
                </v-btn>
            </div>
            <!--            Administration Button-->
            <div v-if="isAuthenticated">
                <v-btn class="ma-3" :to="'/Administration'" color="primary" :disabled="isAdmin === false">
                    Administration
                </v-btn>
            </div>
            <!--            Login Button-->
            <div v-if="isAuthenticated">
                <v-btn text :to="'/Account'"> <v-icon>mdi-account</v-icon> {{ getUsername }} </v-btn>
            </div>
            <div v-else>
                <v-btn text :to="'/Login'"> <v-icon>mdi-account</v-icon> Login </v-btn>
            </div>
            <!--            light/dark mode button-->
            <v-btn icon>
                <v-icon color="primary" @click="setTheme()">mdi-brightness-4</v-icon>
            </v-btn>
        </v-app-bar>
        <!--content of the individual page-->
        <v-content>
            <v-container>
                <nuxt />
            </v-container>
        </v-content>
        <!--footer-->
        <v-footer :fixed="fixed" app>
            <span>Reply &copy; {{ new Date().getFullYear() }}</span>
            <v-btn text small color="primary" to="/help">Hilfe</v-btn>
            <v-btn text small color="primary" to="/privacy">Datenschutz</v-btn>
            <v-btn text small color="primary" to="/terms">Impressum</v-btn>
        </v-footer>
    </v-app>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
    data() {
        return {
            goDark: false,
            fixed: false,
            items: [
                {
                    icon: 'mdi-pencil',
                    title: 'Umfragen',
                    to: '/polls',
                },
                {
                    icon: 'mdi-account-multiple',
                    title: 'Administration',
                    to: '/Administration',
                },
            ],
            title: 'Umfrato',
        }
    },
    computed: {
        /**
         * returns a css compatible string that sets the background to the color specified in th nuxt.config.js
         */
        backgroundColor() {
            if (this.goDark) {
                return 'background:' + this.$vuetify.theme.themes.dark.background
            } else {
                return 'background:' + this.$vuetify.theme.themes.light.background
            }
        },
        ...mapGetters({
            isEditor: 'account/getIsEditor',
            isAdmin: 'account/getIsAdmin',
            isAuthenticated: 'login/isAuthenticated',
            getUsername: 'login/getUsername',
        }),
    },
    /**
     * Switches the boolean stored in go dark and sets the theme in the nuxt.config.js
     * (this method is called, when the user switches the mode (light/dark)
     */
    methods: {
        setTheme() {
            this.goDark = !this.goDark
            if (this.goDark === true) {
                return (this.$vuetify.theme.dark = true)
            } else {
                return (this.$vuetify.theme.dark = false)
            }
        },
    },
}
</script>
