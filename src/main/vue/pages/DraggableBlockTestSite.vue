<template>
    <div>
        <v-card>
            <v-container>
                <v-row no-gutters>
                    <v-col>
                        <v-toolbar color="light-blue" dark>
                            <v-toolbar-title>DraggableBlocks</v-toolbar-title>
                            <v-spacer></v-spacer>
                        </v-toolbar>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col>
                        <draggable-block></draggable-block>
                    </v-col>
                </v-row>
            </v-container>
        </v-card>
    </div>
</template>

<script>
import DraggableBlock from '../components/draggableBlock'
export default {
    name: 'DraggableBlockTestSite',
    components: { DraggableBlock },
}
</script>

<style scoped></style>
