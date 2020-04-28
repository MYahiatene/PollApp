<template>
    <v-container>
        <v-subheader class="display-2"> Umfrage 1</v-subheader>
        <v-content>
            <v-row>
                <v-col cols="4">
                    <v-container fluid>
                        <v-flex class="ma-n4">
                            <v-list v-for="categorie in categories" :key="categorie.id" two-line>
                                <v-subheader class="headline"> {{ categorie.name }} </v-subheader>
                                <draggable v-model="categorie.items" group="questions">
                                    <v-list v-for="item in categorie.items" :key="item.id">
                                        <v-list-tile :key="item.title">
                                            <v-list-tile-content>
                                                <v-list-tile-title>{{ item.title }}</v-list-tile-title>
                                            </v-list-tile-content>
                                            <v-list-item-action>
                                                <v-btn depressed small icon>
                                                    <v-icon>{{ item.symbol }}</v-icon>
                                                </v-btn>
                                            </v-list-item-action>
                                        </v-list-tile>
                                    </v-list>
                                </draggable>
                            </v-list>
                        </v-flex>
                    </v-container>
                    <div class="ma-8" @click="createSurvey()">
                        <v-btn depressed small>Neue Kategorie</v-btn>
                    </div>
                </v-col>
                <v-col cols="8">
                    <QuestionBuildWidget></QuestionBuildWidget>
                </v-col>
            </v-row>
        </v-content>
    </v-container>
</template>

<script>
    import QuestionBuildWidget from '../components/QuestionBuildWidget'

    export default {
        name: 'DragDropTest',
        components: { QuestionBuildWidget },
        data: () => ({
            categories: [
                {
                    id: '1',
                    name: 'Hauptkategorie',
                    items: [
                        {
                            id: '1',
                            title: 'Frage 1',
                            symbol: 'mdi-pencil-outline'
                    },
                    {
                        id: '2',
                        title: 'Frage 2',
                        symbol: 'mdi-pencil-outline',
                    },
                ],
            },
            {
                id: '2',
                name: 'Kategorie 1',
                items: [
                    {
                        id: '3',
                        title: 'Frage 3',
                        symbol: 'mdi-pencil-outline',
                    },
                    {
                        id: '4',
                        title: 'Frage 4',
                        symbol: 'mdi-pencil-outline',
                    },
                    {
                        id: '5',
                        title: 'Frage 5',
                        symbol: 'mdi-pencil-outline',
                    },
                ],
            },
        ],
    }),
    methods: {
        createSurvey() {},
    },
}
</script>
