<template>
    <div class="text-center">
        <v-dialog v-model="dialog" overlay-color="background" width="800" overlay-opacity="0.75">
            <template v-slot:activator="{ on }">
                <v-tooltip bottom>
                    <template v-slot:activator="{ attrs }">
                        <v-btn
                            class="ma-2"
                            large
                            color="primary"
                            v-bind="attrs"
                            v-on="on"
                            @click="visualSettings = !visualSettings"
                        >
                            <v-icon>mdi-cloud-upload</v-icon>
                        </v-btn>
                    </template>
                    <span>Umfrage Importieren</span>
                </v-tooltip>
            </template>
            <v-card class="pa-3">
                <v-card-title> Umfrage importieren </v-card-title>
                Wählen sie die zu Importierende Umfrage:
                <v-file-input v-model="file" prepend-icon="mdi-file" show-size accept=".json, .JSON" />
                <v-btn color="accent" :disabled="!file" @click="uploadFile">Importieren </v-btn>
            </v-card>
        </v-dialog>
    </div>
</template>
<script>
import { mapActions } from 'vuex'

export default {
    name: 'ImportWidget',
    data() {
        return {
            file: null,
            dialog: false,
        }
    },
    methods: {
        ...mapActions({ initialize: 'evaluation/initialize', updateData: 'evaluation/updateData' }),
        uploadFile() {
            const reader = new FileReader()
            reader.readAsText(this.file)
            reader.onload = async (e) => {
                // console.log('File: ', this.file)
                // console.log('FileE: ', e.target.result)
                if (await this.$store.dispatch('evaluation/importPoll', e.target.result)) {
                    this.dialog = false
                    this.$emit('done')
                }
            }
        },
    },
}
</script>

<style scoped></style>
