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
                        <v-toolbar class="mb-1">
                            <v-text-field
                                v-model="search"
                                clearable
                                flat
                                solo-inverted
                                hide-details
                                prepend-inner-icon="mdi-pencil"
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
                                    prepend-inner-icon="mdi-pencil"
                                    label="Sort by"
                                ></v-select>
                                <v-spacer></v-spacer>
                                <v-btn class="ma-3" nuxt to="/PollCreation" color="primary">
                                    neu
                                </v-btn>
                                <v-btn-toggle v-model="sortDesc" mandatory>
                                    <v-btn large depressed :value="false">
                                        <v-icon>mdi-arrow-up</v-icon>
                                    </v-btn>
                                    <v-btn large depressed :value="true">
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
                                            <v-list-item-content :class="{ 'teal--text': sortBy === key }"
                                                >{{ key }}:</v-list-item-content
                                            >
                                            <v-list-item-content
                                                class="align-end"
                                                :class="{ 'teal--text': sortBy === key }"
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
                { title: 'Umfrage1', id: '0', icon: 'mdi-pencil', active: true, editable: false },
                { title: 'Umfrage2', id: '1', icon: 'mdi-pencil', active: false, editable: false },
                { title: 'Umfrage3', id: '2', icon: 'mdi-pencil', active: false, editable: false },
                { title: 'Umfrage4', id: '3', icon: 'mdi-pencil', active: false, editable: false },
                { title: 'Umfrage5', id: '4', icon: 'mdi-pencil', active: false, editable: false },
                { title: 'Umfrage6', id: '5', icon: 'mdi-pencil', active: false, editable: false },
                { title: 'Umfrage7', id: '6', icon: 'mdi-pencil', active: false, editable: false },
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
