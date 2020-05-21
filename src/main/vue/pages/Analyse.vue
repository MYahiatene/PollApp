<template>
    <div>
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-layout v-else>
            <v-flex class="text-center">
                <div>
                    <v-card>
                        <v-container>
                            <v-row no-gutters>
                                <v-col>
                                    <h1>Auswertungsseite</h1>
                                </v-col>
                                <v-col align="right">
                                    <BlockDialog></BlockDialog>
                                </v-col>
                            </v-row>
                            <v-row>
                                <v-col>
                                    <bar-chart></bar-chart>
                                </v-col>
                            </v-row>
                        </v-container>
                    </v-card>
                </div>
            </v-flex>
        </v-layout>
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
import AuthGate from '../components/AuthGate'
import BarChart from '../components/BarChart'
import BlockDialog from '../components/BlockDialog'
export default {
    components: { AuthGate, BlockDialog, BarChart },
    computed: {
        ...mapGetters({
            isAuthenticated: 'login/isAuthenticated',
        }),
    },
}
</script>
