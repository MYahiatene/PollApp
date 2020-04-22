<template>
    <v-card height="350px">
        <v-navigation-drawer absolute permanent right>
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
        <v-toolbar color="light-blue" dark>
            <v-app-bar-nav-icon></v-app-bar-nav-icon>

            <v-toolbar-title>Meine Umfragen</v-toolbar-title>

            <v-spacer></v-spacer>
        </v-toolbar>

        <v-list two-line subheader>
            <v-list-item v-for="item in items2" :key="item.title" link>
                <v-list-item-content>
                    <v-icon v-text="item.icon"></v-icon>
                    <v-list-item-title v-text="item.title"></v-list-item-title>
                    <v-list-item-subtitle v-text="item.subtitle"></v-list-item-subtitle>
                </v-list-item-content>
            </v-list-item>

            <v-divider inset></v-divider>
        </v-list>
    </v-card>
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
                { title: 'Umfrage 1', id: '0', icon: 'mdi-baguette' },
                { title: 'Umfrage 2', id: '1', icon: 'mdi-baguette' },
                { title: 'Umfrage 3', id: '2', icon: 'mdi-baguette' },
            ],
        }
    },
}
</script>
