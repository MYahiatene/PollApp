<template>
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
</template>
<script>
import BarChart from '../components/BarChart'
import BlockDialog from '../components/BlockDialog'
export default {
    components: { BlockDialog, BarChart },
}
</script>
