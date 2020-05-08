<template>
    <v-container grid-list-md text-xs-center>
        <v-layout row wrap>
            <div class="row">
                <div class="col-12">
                    <draggable
                        v-model="rows"
                        tag="v-layout"
                        class="row wrap fill-height align-center sortable-list"
                        style="background: grey;"
                    >
                        <v-flex
                            v-for="row in rows"
                            :key="row.index"
                            class="sortable"
                            xs12
                            my-2
                            style="background: #f46400;"
                        >
                            <draggable
                                :list="row.items"
                                tag="v-layout"
                                :group="{ name: 'row' }"
                                class="row wrap justify-space-around"
                            >
                                <v-flex v-for="item in row.items" :key="item.title" xs4 pa-3 class="row-v">
                                    <!--xs4 pa-3-->
                                    <v-card style="height: 100px;">{{ item.title }}</v-card>
                                </v-flex>
                            </draggable>
                        </v-flex>
                    </draggable>
                </div>

                <!--<rawDisplayer class="col-3" :value="rows" title="List" />-->
            </div>
        </v-layout>
    </v-container>
</template>

<script>
import draggable from 'vuedraggable'
import 'vuetify/dist/vuetify.min.css'

export default {
    name: 'Functional',
    display: 'Functional third party',
    order: 17,
    components: {
        draggable,
    },
    data() {
        return {
            enabled: true,
            rows: [
                {
                    index: 1,
                    items: [
                        {
                            title: 'Männlich, Alter > 30',
                        },
                    ],
                },
                {
                    index: 2,
                    items: [
                        {
                            title: 'Weiblich, Alter < 20',
                        },
                        {
                            title: 'Frage 2: Ja',
                        },
                    ],
                },
                {
                    index: 3,
                    items: [
                        {
                            title: 'Frage 1: Nein',
                        },
                    ],
                },
            ],
        }
    },
}
</script>
<style scoped>
.buttons {
    margin-top: 35px;
}
.row-v {
    height: 150px;
    width: 200px;
}
.ghost {
    opacity: 0.5;
    background: #c8ebfb;
}
</style>
