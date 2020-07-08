<template>
    <v-dialog v-model="dialog" overlay-color="background" width="800" overlay-opacity="0.75">
        <template v-slot:activator="{ on }">
            <v-btn class="ma-2" icon color="primary" v-on="on">
                <v-icon>
                    mdi-cloud-download
                </v-icon>
            </v-btn>
        </template>
        <export-widget :exportResult="exportResult" @done="dialog = false" />
    </v-dialog>
</template>

<script>
import ExportWidget from './exportWidget'
export default {
    name: 'exportWidgetDialog',
    components: { ExportWidget },
    props: {
        exportResult: {
            type: Boolean,
            default: false,
        },
    },
    data() {
        return {
            dialog: false,
        }
    },
}
</script>

<style scoped></style>
