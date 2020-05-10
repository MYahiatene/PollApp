<template>
    <div>
        <v-dialog persistent v-model="dialog" width="500" overlay-opacity="0.95" fullscreen>
            <template v-slot:activator="{ on }">
                <v-btn color="#444444" v-on="on">
                    Analysieren
                </v-btn>
            </template>

            <v-card>
                <v-card-title class="#222222" primary-title>
                    Analyse
                    <v-col align="right">
                        <v-btn color="#444444" @click="dialog = false">
                            Anwenden
                        </v-btn>
                    </v-col>
                </v-card-title>
                <draggable-block></draggable-block>
            </v-card>
        </v-dialog>
    </div>
</template>

<script>
import DraggableBlock from '../components/draggableBlock'
export default {
    name: 'BlockDialog',
    components: { DraggableBlock },
    data() {
        return {
            dialog: false,
        }
    },
}
</script>

<style scoped></style>
