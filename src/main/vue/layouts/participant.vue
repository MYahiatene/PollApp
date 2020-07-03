<template>
    <!--Only create Page, after the PollData arrived -->
    <div v-if="getPoll[1] !== undefined">
        <!--In this file we define the header that will be displayed on all pages in the entire app -->
        <v-app :dark="setTheme" :style="backgroundColor">
            <!--set Theme returns a boolean value depending on the theme the user wants (light=false, dark=true)-->
            <!--backgroundColor is a computed string that sets the background to the color defined by the creator, if
            something was choosen, else defined in the nuxt.config.js depending on the chosen theme-->
            <v-app-bar fixed app>
                <!--Logo and Name of the App-->
                <nuxt-link to="/" style="text-decoration: none;">
                    <div>
                        <h1><img src="LogoEinfach.svg" alt="Logo" width="25" /> {{ title }}</h1>
                    </div>
                </nuxt-link>

                <v-spacer></v-spacer>
                <!--            Buttons defined in items-->
                <div v-for="(item, i) in items" :key="i" router exact>
                    <v-btn class="ma-3" :to="item.to" color="primary" :disabled="isAuthenticated !== true">
                        {{ item.title }}
                    </v-btn>
                </div>
                <!--            Login Button-->
                <div v-if="isAuthenticated">
                    <v-btn text :to="'/Login'"> <v-icon>mdi-account</v-icon> {{ getUsername }} </v-btn>
                </div>
                <div v-else>
                    <v-btn text :to="'/Login'"> <v-icon>mdi-account</v-icon> Login </v-btn>
                </div>
                <!--            light/dark mode button-->
                <v-btn icon>
                    <v-icon color="primary" @click="setTheme()">{{ modeIcon }}</v-icon>
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
                <span>&copy; {{ new Date().getFullYear() }}</span>
            </v-footer>
        </v-app>
    </div>
</template>

<script>
import { mdiBrightness4 } from '@mdi/js'
import { mapGetters } from 'vuex'
export default {
    data() {
        return {
            id: 0,
            poll: null,
            goDark: false,
            fixed: false,
            modeIcon: mdiBrightness4,
            items: [
                {
                    icon: 'mdi-pencil',
                    title: 'Umfragen',
                    to: '/navigation',
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
    /**
     * Calls showPoll (below mapAction) to get PollData for background color.
     */
    mounted() {
        this.id = this.$route.params.id
        this.showPoll()
    },
    computed: {
        /**
         * Returns a css compatible string that sets the background to the color specified during the creation of the
         * poll by the user. If they haven't choosen one, the color specified in the nuxt.config.js is used.
         */
        backgroundColor() {
            if (this.getPoll[1].data.backgroundColor !== null) {
                return 'background:' + this.getPoll[1].data.backgroundColor // '#c42843' // insert picked color here
            } else if (this.goDark) {
                return 'background:' + this.$vuetify.theme.themes.dark.background
            } else {
                return 'background:' + this.$vuetify.theme.themes.light.background
            }
        },
        ...mapGetters({
            isAuthenticated: 'login/isAuthenticated',
            getUsername: 'login/getUsername',
            getPoll: 'participant/getPoll',
        }),
    },
    methods: {
        /**
         * Returns a boolean of wheater or not the dark mode is valid.
         * @returns {boolean} darkMode
         */
        setTheme() {
            this.goDark = !this.goDark
            if (this.goDark === true) {
                return (this.$vuetify.theme.dark = true)
            } else {
                return (this.$vuetify.theme.dark = false)
            }
        },
        /**

         * Calls showPoll in store/participant to get PollData for the background color.

         */
        showPoll() {
            this.$store.dispatch('participant/showPoll', this.id)
        },
    },
}
</script>
