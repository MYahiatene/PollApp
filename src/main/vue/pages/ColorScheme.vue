<template>
    <v-layout>
        <v-flex class="text-center">
            <v-card class="pa-5">
                <h1>Farben im jetzigen Mode:</h1>

                <v-btn color="primary">
                    primary
                </v-btn>

                <v-btn color="secondary">
                    secondary
                </v-btn>

                <v-btn color="info">
                    info
                </v-btn>

                <v-btn color="accent">
                    accent
                </v-btn>

                <v-btn color="success">
                    success
                </v-btn>

                <v-btn color="warning">
                    warning
                </v-btn>

                <v-btn color="error">
                    error
                </v-btn>

                <v-spacer />
                <v-card-text>
                    <v-slider
                        v-model="value"
                        append-icon="mdi-plus"
                        prepend-icon="mdi-minus"
                        @click:append="zoomIn"
                        @click:prepend="zoomOut"
                    ></v-slider>
                </v-card-text>
            </v-card>
        </v-flex>
    </v-layout>
</template>

<script>
export default {
    name: 'ColorScheme',
    data() {
        return {
            value: 0,
        }
    },
    methods: {
        zoomOut() {
            this.value = this.value - 10 || 0
        },
        zoomIn() {
            this.value = this.value + 10 || 100
        },
    },
}
</script>

<style scoped></style>
