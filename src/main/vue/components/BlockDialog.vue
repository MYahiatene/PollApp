<template>
    <div>
        <v-dialog persistent v-model="dialog" width="500" overlay-opacity="0.95" fullscreen>
            <template v-slot:activator="{ on }">
                <v-btn color="primary" v-on="on">
                    Analyse
                </v-btn>
            </template>

            <v-card>
                <v-card-title primary-title>
                    Analyse
                    <v-col align="right">
                        <v-btn color="primary" @click="dialog = false">
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
