<template>
    <v-app light>
        <v-app-bar fixed app>
            <v-toolbar-title v-text="title" />
            <v-spacer />

            <v-div v-for="(item, i) in items" :key="i" router exact>
                <v-btn class="ma-3" :to="item.to" color="primary">
                    {{ item.title }}
                </v-btn>
            </v-div>
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
                    icon: 'mdi-apps',
                    title: 'Start',
                    to: '/',
                },
                {
                    icon: 'mdi-account',
                    title: 'Login',
                    to: '/Login',
                },
                {
                    icon: 'mdi-account-multiple',
                    title: 'Administration',
                    to: '/Administration',
                },
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
            ],

            title: 'Umfrato',
        }
    },
}
</script>
