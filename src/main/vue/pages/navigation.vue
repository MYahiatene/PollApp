<template>
    <v-container grid-list-md text-xs-center>
        <v-layout row wrap>
            <v-flex>
                <v-card>
                    <v-toolbar color="light-blue" dark>
                        <v-toolbar-title>Bearbeitbare Umfragen</v-toolbar-title>
                        <v-spacer></v-spacer>
                    </v-toolbar>
                    <v-list two-line subheader>
                        <v-list-item v-for="item in items2" :key="item.title" link>
                            <v-list-item-avatar>
                                <v-icon></v-icon>
                            </v-list-item-avatar>
                            <v-list-item-content>
                                <v-list-item-title v-text="item.title"></v-list-item-title>
                                <v-list-item-subtitle v-text="item.subtitle"></v-list-item-subtitle>
                            </v-list-item-content>
                            <v-list-item-action>
                                <v-btn icon>
                                    <v-icon color="grey lighten-1">mdi-baguette</v-icon>
                                </v-btn>
                            </v-list-item-action>
                            <v-list-item-action>
                                <v-btn icon>
                                    <v-icon color="grey lighten-1">mdi-play</v-icon>
                                </v-btn>
                            </v-list-item-action>
                            <!--<v-list-item-icon>
                                <v-icon v-text="item.icon"></v-icon>
                            </v-list-item-icon>-->
                        </v-list-item>
                    </v-list>
                </v-card>
            </v-flex>
            <v-flex>
                <v-card>
                    <v-toolbar color="light-blue" dark>
                        <v-toolbar-title>Aktive Umfragen</v-toolbar-title>
                        <v-spacer></v-spacer>
                    </v-toolbar>
                    <v-list two-line subheader>
                        <v-list-item v-for="item in items4" :key="item.title" link>
                            <v-list-item-avatar>
                                <v-icon></v-icon>
                            </v-list-item-avatar>
                            <v-list-item-content>
                                <v-list-item-title v-text="item.title"></v-list-item-title>
                                <v-list-item-subtitle v-text="item.subtitle"></v-list-item-subtitle>
                            </v-list-item-content>
                            <v-list-item-action>
                                <v-btn icon>
                                    <v-icon color="grey lighten-1">mdi-information</v-icon>
                                </v-btn>
                            </v-list-item-action>
                            <v-list-item-action>
                                <v-btn icon>
                                    <v-icon color="grey lighten-1">mdi-stop</v-icon>
                                </v-btn>
                            </v-list-item-action>
                            <!--<v-list-item-icon>
                                <v-icon v-text="item.icon"></v-icon>
                            </v-list-item-icon>-->
                        </v-list-item>
                    </v-list>
                </v-card>
            </v-flex>
            <v-flex>
                <v-card>
                    <v-toolbar color="light-blue" dark>
                        <v-toolbar-title>Inaktive Umfragen</v-toolbar-title>
                        <v-spacer></v-spacer>
                    </v-toolbar>
                    <v-list two-line subheader>
                        <v-list-item v-for="item in items3" :key="item.title" link>
                            <v-list-item-avatar>
                                <v-icon></v-icon>
                            </v-list-item-avatar>
                            <v-list-item-content>
                                <v-list-item-title v-text="item.title"></v-list-item-title>
                                <v-list-item-subtitle v-text="item.subtitle"></v-list-item-subtitle>
                            </v-list-item-content>
                            <v-list-item-action>
                                <v-btn icon>
                                    <v-icon color="grey lighten-1">mdi-information</v-icon>
                                </v-btn>
                            </v-list-item-action>
                            <v-list-item-action>
                                <v-btn icon>
                                    <v-icon color="grey lighten-1">mdi-play</v-icon>
                                </v-btn>
                            </v-list-item-action>
                            <!--<v-list-item-icon>
                                <v-icon v-text="item.icon"></v-icon>
                            </v-list-item-icon>-->
                        </v-list-item>
                    </v-list>
                </v-card>
            </v-flex>
            <v-card class="d-inline-flex">
                <v-navigation-drawer permanent right expand-on-hover fixed :mini-variant.sync="mini">
                    <template v-slot:prepend>
                        <v-list-item two-line>
                            <v-list-item-content>
                                <v-list-item-title>Annabelle Bebenroth</v-list-item-title>
                                <v-list-item-subtitle>Administrator</v-list-item-subtitle>
                            </v-list-item-content>
                        </v-list-item>
                    </template>
                    <v-divider></v-divider>
                    <v-list dense>
                        <v-list-item v-for="item in items" :key="item.title" link>
                            <v-list-item-icon>
                                <v-icon>{{ item.icon }}</v-icon>
                            </v-list-item-icon>
                            <v-list-item-content>
                                <v-list-item-title>{{ item.title }}</v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                    </v-list>
                </v-navigation-drawer>
            </v-card>
        </v-layout>
    </v-container>
</template>
<script>
export default {
    data() {
        return {
            items: [
                { title: 'Home', icon: 'mdi-home-city' },
                { title: 'Meine Umfragen', icon: 'mdi-account' },
            ],
            items2: [
                { title: 'Umfrage 1', id: '0', icon: 'mdi-baguette', icon2: 'mdi-home-city' },
                { title: 'Umfrage 2', id: '1', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
            ],
            items3: [
                { title: 'Umfrage 1', id: '0', icon: 'mdi-baguette', icon2: 'mdi-home-city' },
                { title: 'Umfrage 2', id: '1', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
            ],
            items4: [
                { title: 'Umfrage 1', id: '0', icon: 'mdi-baguette', icon2: 'mdi-home-city' },
                { title: 'Umfrage 2', id: '1', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 2', id: '1', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 2', id: '1', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 2', id: '1', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
            ],
        }
    },
}
</script>
