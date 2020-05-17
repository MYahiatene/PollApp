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
                <v-btn class="ma-3" :to="item.to" color="primary">
                    {{ item.title }}
                </v-btn>
            </v-div>
            <v-btn text :to="'/Login'"> <v-icon>mdi-account</v-icon> Login </v-btn>
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
export default {
    data() {
        return {
            fixed: false,
            items: [
                {
                    icon: 'mdi-pencil',
                    title: 'Konfiguration',
                    to: '/Configuration',
                },
                {
                    icon: 'mdi-chart-bubble',
                    title: 'Auswertung',
                    to: '/Analyse',
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
}
</script>
