<template>
    <v-layout>
        <v-flex class="text-center">
            <v-card class="pa-5">
                <h1>Farben im jetzigen Mode:</h1>

                <v-btn color="primary"> primary </v-btn>

                <v-btn color="secondary"> secondary </v-btn>

                <v-btn color="info"> info </v-btn>

                <v-btn color="accent"> accent </v-btn>

                <v-btn color="success"> success </v-btn>

                <v-btn color="warning"> warning </v-btn>

                <v-btn color="error"> error </v-btn>

                <v-spacer />
            </v-card>
            <v-card class="ma-1 pa-1">
                <div v-for="(color, index) in colors" :key="index">
                    <v-card :style="color.color" class="pa-10 ma-1">
                        {{ color.name }}
                    </v-card>
                </div>
            </v-card>
        </v-flex>
    </v-layout>
</template>

<script>
export default {
    name: 'ColorScheme',

    computed: {
        colors() {
            return [
                { name: 'softAccent', color: 'backgroundColor:' + this.$vuetify.theme.currentTheme.softAccent + ';' },
                { name: 'background2', color: 'backgroundColor:' + this.$vuetify.theme.currentTheme.background2 + ';' },
                { name: 'header', color: 'backgroundColor:' + this.$vuetify.theme.currentTheme.header + ';' },
            ]
        },
    },
}
</script>

<style scoped></style>
