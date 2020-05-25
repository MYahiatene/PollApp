<template>
    <div>
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-container v-else-if="items[0].pollId !== undefined" grid-list-md text-xs-center>
            <v-layout row wrap>
                <v-container fluid>
                    <v-data-iterator
                        :items="items"
                        :search="search"
                        :sort-by="sortBy"
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

                        <template v-slot:default="props">
                            <v-row>
                                <v-col v-for="item in props.items" :key="item.pollId" cols="12" sm="6" md="4" lg="3">
                                    <v-card>
                                        <v-card-title class="subheading font-weight-bold"
                                            >#{{ item.pollId }} {{ item.pollName }}
                                            <v-spacer></v-spacer>
                                            <v-btn icon @click="activatePoll(props.items.indexOf(item))">
                                                <v-icon>{{ getStatusIcon(item.pollStatus) }}</v-icon>
                                            </v-btn>
                                            <v-btn icon nuxt :to="generateLink(item.pollStatus, item.pollId)">
                                                <v-icon>{{ getActionIcon(item.pollStatus) }}</v-icon>
                                            </v-btn>
                                            <v-menu bottom left>
                                                <template v-slot:activator="{ on }">
                                                    <v-btn icon v-on="on">
                                                        <v-icon>mdi-dots-vertical</v-icon>
                                                    </v-btn>
                                                </template>

                                                <v-list>
                                                    <v-list-item
                                                        v-for="(action, i) in contextActions"
                                                        :key="i"
                                                        :to="action.link"
                                                    >
                                                        <v-list-item-title>{{ action.title }}</v-list-item-title>
                                                    </v-list-item>
                                                </v-list>
                                            </v-menu>
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
                                    </v-card>
                                </v-col>
                            </v-row>
                        </template>
                    </v-data-iterator>
                </v-container>
            </v-layout>
        </v-container>
        <v-container v-else>
            <v-card>
                <v-card-title>
                    Der Server antwortet nicht
                </v-card-title>
            </v-card>
        </v-container>
    </div>
</template>
<script>
import { mapActions, mapGetters } from 'vuex'
import AuthGate from '../components/AuthGate'
export default {
    name: 'navigation',
    components: { AuthGate },
    data() {
        return {
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
            if (key === 'categoryList') {
                return item.categoryList.length
            } else if (key === 'questionCount') {
                let questionNumber = 0
                for (let i = 0; i < item.categoryList.length; i++) {
                    questionNumber = questionNumber + item.categoryList[i].questionList.length
                }
                return questionNumber
            } else {
                return item[key]
            }
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
    },
}
</script>
