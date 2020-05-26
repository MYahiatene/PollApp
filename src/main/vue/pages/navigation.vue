<template>
    <v-container v-if="items !== undefined" grid-list-md text-xs-center>
        <v-layout row wrap>
            <v-container fluid>
                <v-data-iterator
                    :items="items"
                    :search="search"
                    :sort-by="sortBy.toLowerCase()"
                    :sort-desc="sortDesc"
                    hide-default-footer
                >
                    <template v-slot:header>
                        <v-toolbar class="mb-1">
                            <v-btn large color="primary" to="/PollCreation">
                                <v-icon>mdi-plus</v-icon>
                            </v-btn>
                            <v-spacer />
                            <v-text-field
                                v-model="search"
                                clearable
                                hide-details
                                prepend-inner-icon="mdi-magnify"
                                label="Suchen"
                            ></v-text-field>
                            <template v-if="$vuetify.breakpoint.mdAndUp">
                                <v-spacer></v-spacer>
                                <v-select
                                    v-model="sortBy"
                                    clearable
                                    hide-details
                                    :items="pollKeys"
                                    prepend-inner-icon="mdi-sort"
                                    label="Sortiere nach"
                                ></v-select>
                                <v-spacer></v-spacer>
                                <v-btn-toggle v-model="sortDesc" mandatory>
                                    <v-btn large depressed :value="false">
                                        <v-icon>mdi-sort-ascending</v-icon>
                                    </v-btn>
                                    <v-btn large depressed :value="true">
                                        <v-icon>mdi-sort-descending</v-icon>
                                    </v-btn>
                                </v-btn-toggle>
                            </template>
                        </v-toolbar>
                    </template>

                    <v-container>
                        <template>
                            <v-data-table
                                :headers="headers"
                                :items="items"
                                :search="search"
                                :custom-filter="filterOnlyCapsText"
                                hide-actions
                                class="elevation-1"
                            >
                                <!--<thead>
                                        <tr>
                                            <th class="text-left">Name</th>
                                            <th class="text-left">PollIDs</th>
                                            <th class="text-left">PollCreator</th>
                                            <th class="text-left">CreationDate</th>
                                            <th class="text-left">ActivatedDate</th>
                                            <th class="text-left">DeactivatedDate</th>
                                            <th class="text-left">Anonymitätsgrad</th>
                                            <th class="text-left">Status</th>
                                            <th class="text-left">Kategorienliste</th>
                                            <th class="text-left">Fragenanzahl</th>
                                        </tr>
                                    </thead>-->
                                <!--<td v-for="header in headers">
                                        {{ myprops.item[header.value] }}
                                    </td>-->
                                <!--<template slot="items">
                                    <td
                                        v-for="(key, index) in filteredKeys"
                                        :key="index"
                                        :class="{ 'teal--text': sortBy === key }"
                                    >
                                        {{ key }}
                                    </td>
                                </template>-->
                                <!--<td :class="{ 'teal--text': sortBy === key }">{{ key }}:</td>
                                            <td class="align-end" :class="{ 'teal--text': sortBy === key }">
                                                {{ items[index][key.toLowerCase()] }}
                                            </td>-->
                                <!--<tr v-for="item in desserts" :key="item.pollId">
                                            <td class="text-left">{{ item.name }}</td>
                                            <td class="text-left">{{ item.pollId }}</td>
                                            <td class="text-left">{{ item.pollCreator }}</td>
                                            <td class="text-left">{{ item.creationDate }}</td>
                                            <td class="text-left">{{ item.activatedAt }}</td>
                                            <td class="text-left">{{ item.deactivatedAt }}</td>
                                            <td class="text-left">{{ item.anonymityStatus }}</td>
                                            <td class="text-left">{{ item.status }}</td>
                                        </tr>-->
                            </v-data-table>
                        </template>
                    </v-container>

                    <!--<template>
                        <div>{{ itemsList }}</div>
                        <v-data-table :items="itemsList" class="elevation-1" hide-actions hide-headers>
                            <template slot="items" slot-scope="props">
                                <td>@{{ props.item.name }}</td>
                                <td class="text-xs-right">@{{ props.items }}</td>
                            </template>
                        </v-data-table>
                    </template>-->
                    <!--<template>
                        <v-card>
                            <v-simple-table dense>
                                <template v-slot:default="props">
                                    <thead>
                                        <tr>
                                            <th class="text-left">Name</th>
                                            <th class="text-left">PollStatus</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr v-for="item in props.items" :key="item.pollId">
                                            <td>{{ item.pollId }}</td>
                                            <td>{{ item.pollName }}</td>
                                        </tr>
                                    </tbody>
                                </template>
                            </v-simple-table>
                        </v-card>
                    </template>-->
                    <!--<v-card>
                                    <v-card-title class="subheading font-weight-bold"
                                        >#{{ item.pollId }} {{ item.pollName }}
                                        <v-spacer></v-spacer>
                                    </v-card-title>
                                    <v-divider></v-divider>
                                    <v-list dense>
                                        <v-list-item v-for="(key, index) in filteredKeys" :key="index">
                                            <v-list-item-content :class="{ 'teal--text': sortBy === key }"
                                                >{{ translateKey(key) }}:</v-list-item-content
                                            >
                                            <v-list-item-content
                                                class="align-end"
                                                :class="{ 'teal--text': sortBy === key }"
                                                >{{ showValue(item, key) }}</v-list-item-content
                                            >
                                        </v-list-item>
                                    </v-list>
                                </v-card>-->
                </v-data-iterator>
            </v-container>
        </v-layout>
    </v-container>
</template>
<script>
import { mapActions, mapGetters } from 'vuex'
import AuthGate from '../components/AuthGate'
export default {
    name: 'navigation',
    components: { AuthGate },
    data() {
        return {
            itemsList: [],
            search: '',
            filter: {},
            sortDesc: false,
            sortBy: '',
            contextActions: [
                { title: 'Beantworten', link: '/' },
                { title: 'Bearbeiten', link: '/QuestionOverview' },
                { title: 'Aktivieren', link: '/' },
                { title: 'Löschen', link: '/' },
            ],
            headers: [
                {
                    text: 'Poll Creator',
                    value: 'pollCreator',
                },
                { text: 'Poll ID', value: 'pollId' },
                { text: 'Creation Date', value: 'creationDate' },
                { text: 'Status', value: 'status' },
                { text: 'questionCount', value: 'questionCount' },
                { text: 'FuckThis', value: 'anonymityStatus' },
            ],
            items: [
                {
                    name: 'Frozen Yogurt',
                    pollCreator: 'tbrettmann',
                    pollId: '159',
                    creationDate: '1945',
                    activatedDate: '1945',
                    deactivatedDate: '1990',
                    anonymityStatus: '1',
                    status: 2,
                    questionCount: 1,
                },
                {
                    name: 'Ice cream sandwich',
                    pollCreator: 'kony2012',
                    pollId: '237',
                    creationDate: '1945',
                    activatedDate: '1945',
                    deactivatedDate: '1990',
                    anonymityStatus: '1',
                    status: '2012',
                    questioncount: '9000',
                },
            ],
        }
    },
    computed: {
        ...mapGetters({
            items: 'navigation/getPolls',
            isAuthenticated: 'login/isAuthenticated',
        }),
        pollKeys() {
            const keys = []
            for (const k in this.items[0]) keys.push(k)
            keys.push('questionCount')
            return keys
        },
        filteredKeys() {
            return this.pollKeys.filter((key) => key !== `pollName` && key !== 'pollId')
        },
    },
    mounted() {
        console.log(this.items)
        this.initialize()
    },

    methods: {
        ...mapActions({ initialize: 'navigation/initialize' }),
        getActionIcon(active) {
            if (active) {
                return 'mdi-magnify'
            } else {
                return 'mdi-pencil'
            }
        },
        activatePoll(id) {
            console.log('Fake activate poll at ' + id)
        },
        getStatusIcon(active) {
            if (active) {
                return 'mdi-stop'
            } else {
                return 'mdi-play'
            }
        },
        generateLink(active, id) {
            if (active) {
                return '/BaseEvaluationPage'
            } else {
                return '/QuestionOverview'
            }
        },
        showValue(item, key) {
            alert(item[key])
            return item[key]
        },

        translateKey(key) {
            switch (key) {
                case 'pollCreator':
                    return 'Erstellt von'
                case 'creationDate':
                    return 'Erstellt am'
                case 'activatedDate':
                    return 'Aktiviert am'
                case 'deactivatedDate':
                    return 'Deaktiviert am'
                case 'anonymityStatus':
                    return 'Anonymitätsgrad'
                case 'pollStatus':
                    return 'Status'
                case 'categoryList':
                    return 'Kategorien'
                case 'questionCount':
                    return 'Fragen'
                default:
                    return key
            }
        },
        filterOnlyCapsText(value, search, item) {
            return (
                value != null &&
                search != null &&
                typeof value === 'string' &&
                value.toString().toLowerCase().includes(search.toLowerCase())
            )
        },
    },
}
</script>
