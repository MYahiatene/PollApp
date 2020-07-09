<template>
    <v-card>
        <v-card-title>Umfrato-Hilfehotline</v-card-title>
        <v-card-text>
            Für Hilfe schreiben Sie bitte ein Fax an:
            <v-spacer></v-spacer>
            <v-chip class="ma-2" color="primary">
                <v-icon>mdi-fax</v-icon>
                0521/XXXXXXXXX
            </v-chip>
            <v-spacer></v-spacer>
            Oder rufen Sie uns per Fernsprechapparat unter folgender Nummer an:
            <v-spacer></v-spacer>
            <v-chip class="ma-2" color="primary">
                <v-icon>mdi-phone</v-icon>
                0521/XXXXXXXXX
            </v-chip>
            <v-spacer></v-spacer>
            Wir freuen uns, Ihnen helfen zu dürfen.
        </v-card-text>
    </v-card>
</template>

<script>
export default {
    name: 'Help',
}
</script>

<style scoped></style>
