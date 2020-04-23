<template>
    <v-container>
        <v-subheader class="display-2"> Umfrage 1</v-subheader>
        <v-row>
            <v-col>
                <v-card max-width="400" class="ma-4">
                    <v-list v-for="categorie in categories" :key="categorie.name">
                        <v-list-item>
                            <v-list-item-title class="title">{{ categorie.name }} </v-list-item-title>
                            <v-btn class="mx-2" fab dark x-small>
                                <v-icon>mdi-plus</v-icon>
                            </v-btn>
                        </v-list-item>
                        <v-list-item v-for="item in categorie.items" :key="item.title">
                            <v-list-item-content>
                                <v-list-item-title>{{ item.title }}</v-list-item-title>
                            </v-list-item-content>
                            <v-list-item-action>
                                <v-btn icon>
                                    <v-icon>{{ item.symbol }}</v-icon>
                                </v-btn>
                            </v-list-item-action>
                        </v-list-item>
                    </v-list>
                </v-card>
                <div class="ma-4">
                    <v-btn depressed small>Neue Kategorie</v-btn>
                </div>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
export default {
    name: 'QuestionOverview',
    data: () => ({
        categories: [
            {
                name: 'Hauptkategorie',
                items: [
                    {
                        title: 'Frage 1',
                        symbol: 'mdi-pencil-outline',
                    },
                    {
                        title: 'Frage 2',
                        symbol: 'mdi-pencil-outline',
                    },
                ],
            },
            {
                name: 'Kategorie 1',
                items: [
                    {
                        title: 'Frage 3',
                        symbol: 'mdi-pencil-outline',
                    },
                    {
                        title: 'Frage 4',
                        symbol: 'mdi-pencil-outline',
                    },
                    {
                        title: 'Frage 5',
                        symbol: 'mdi-pencil-outline',
                    },
                ],
            },
        ],
    }),
}
</script>

<style scoped></style>
