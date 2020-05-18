<template>
    <v-container grid-list-md text-xs-center>
        <v-layout row wrap>
            <v-container fluid>
                <v-data-iterator
                    :items="items2"
                    :search="search"
                    :sort-by="sortBy.toLowerCase()"
                    :sort-desc="sortDesc"
                    hide-default-footer
                >
                    <template v-slot:header>
                        <v-toolbar dark color="primary" class="mb-1">
                            <v-text-field
                                v-model="search"
                                clearable
                                flat
                                solo-inverted
                                hide-details
                                prepend-inner-icon="mdi-baguette"
                                label="Search"
                            ></v-text-field>
                            <template v-if="$vuetify.breakpoint.mdAndUp">
                                <v-spacer></v-spacer>
                                <v-select
                                    v-model="sortBy"
                                    flat
                                    solo-inverted
                                    hide-details
                                    :items="keys"
                                    prepend-inner-icon="mdi-baguette"
                                    label="Sort by"
                                ></v-select>
                                <v-spacer></v-spacer>
                                <v-btn-toggle v-model="sortDesc" mandatory>
                                    <v-btn large depressed color="secondary" :value="false">
                                        <v-icon>mdi-arrow-up</v-icon>
                                    </v-btn>
                                    <v-btn large depressed color="secondary" :value="true">
                                        <v-icon>mdi-arrow-down</v-icon>
                                    </v-btn>
                                </v-btn-toggle>
                            </template>
                        </v-toolbar>
                    </template>

                    <template v-slot:default="props">
                        <v-row>
                            <v-col v-for="item in props.items" :key="item.id" cols="12" sm="6" md="4" lg="3">
                                <v-card>
                                    <v-card-title class="subheading font-weight-bold">{{ item.title }}</v-card-title>

                                    <v-divider></v-divider>

                                    <v-list dense>
                                        <v-list-item v-for="(key, index) in filteredKeys" :key="index">
                                            <v-list-item-content :class="{ 'blue--text': sortBy === key }"
                                                >{{ key }}:</v-list-item-content
                                            >
                                            <v-list-item-content
                                                class="align-end"
                                                :class="{ 'blue--text': sortBy === key }"
                                                >{{ item[key.toLowerCase()] }}</v-list-item-content
                                            >
                                        </v-list-item>
                                    </v-list>
                                </v-card>
                            </v-col>
                        </v-row>
                    </template>
                </v-data-iterator>
            </v-container>

            <!--<v-flex>
                <v-card>
                    <v-toolbar color="light-blue" dark>
                        <v-toolbar-title>Aktive Umfragen</v-toolbar-title>
                        <v-spacer></v-spacer>
                    </v-toolbar>
                    <v-list two-line subheader>
                        <v-list-item v-for="item in customSort" :key="item.title" link>
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
                        </v-list-item>
                    </v-list>
                </v-card>
            </v-flex>-->
            <!--<v-list>
                    <v-list-item>
                        <v-list-item-icon>
                            <v-icon>mdi-home</v-icon>
                        </v-list-item-icon>

                        <v-list-item-title>Home</v-list-item-title>
                    </v-list-item>

                    <v-list-group no-action sub-group value="true">
                        <template v-slot:activator>
                            <v-list-item-content>
                                <v-list-item-title>Aktive Umfragen</v-list-item-title>
                            </v-list-item-content>
                        </template>

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
                                    <v-icon color="grey lighten-1">mdi-information</v-icon>
                                </v-btn>
                            </v-list-item-action>
                            <v-list-item-action>
                                <v-btn icon>
                                    <v-icon color="grey lighten-1">mdi-stop</v-icon>
                                </v-btn>
                            </v-list-item-action>
                        </v-list-item>
                    </v-list-group>

                    <v-list-group no-action sub-group value="true">
                        <template v-slot:activator>
                            <v-list-item-content>
                                <v-list-item-title>Bearbeitbare Umfragen</v-list-item-title>
                            </v-list-item-content>
                        </template>

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
                        </v-list-item>
                    </v-list-group>
                    <v-list-group no-action sub-group value="true">
                        <template v-slot:activator>
                            <v-list-item-content>
                                <v-list-item-title>Inaktive Umfragen</v-list-item-title>
                            </v-list-item-content>
                        </template>

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
                                    <v-icon color="grey lighten-1">mdi-information</v-icon>
                                </v-btn>
                            </v-list-item-action>
                            <v-list-item-action>
                                <v-btn icon>
                                    <v-icon color="grey lighten-1">mdi-play</v-icon>
                                </v-btn>
                            </v-list-item-action>
                        </v-list-item>
                    </v-list-group>
                </v-list>-->
        </v-layout>
    </v-container>
</template>
<script>
export default {
    data() {
        return {
            search: '',
            filter: {},
            sortDesc: false,
            sortBy: 'name',
            keys: ['Title', 'Editable', 'Active'],
            items2: [
                { title: 'Umfrage1', id: '0', icon: 'mdi-baguette', active: true, editable: false },
                { title: 'Umfrage2', id: '1', icon: 'mdi-baguette', active: false, editable: false },
                { title: 'Umfrage3', id: '2', icon: 'mdi-baguette', active: false, editable: false },
                { title: 'Umfrage4', id: '3', icon: 'mdi-baguette', active: false, editable: false },
                { title: 'Umfrage5', id: '4', icon: 'mdi-baguette', active: false, editable: false },
                { title: 'Umfrage6', id: '5', icon: 'mdi-baguette', active: false, editable: false },
                { title: 'Umfrage7', id: '6', icon: 'mdi-baguette', active: false, editable: false },
            ],
        }
    },
    computed: {
        filteredKeys() {
            return this.keys.filter((key) => key !== `Name`)
        },
    },
}
</script>
