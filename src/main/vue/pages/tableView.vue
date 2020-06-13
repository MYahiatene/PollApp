<template>
    <v-container grid-list-md text-xs-center>
        <v-layout row wrap>
            <v-flex>
                <v-card>
                    <v-toolbar color="light-blue" dark>
                        <v-toolbar-title>Personenbezogene Antworten</v-toolbar-title>
                        <v-spacer></v-spacer>
                    </v-toolbar>
                    <v-data-table
                        :headers="headers"
                        :items="answers"
                        :items-per-page="5"
                        class="elevation-1"
                    ></v-data-table>
                </v-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
export default {
    data() {
        return {
            headers: [
                {
                    text: 'PersonenID',
                    align: 'start',
                    sortable: false,
                    value: 'id',
                },
                { text: 'Name', value: 'name' },
                { text: 'Geburtstag', value: 'birthday' },
                { text: 'Alter', value: 'age' },
                { text: 'Geschlecht', value: 'gender' },
                { text: 'Antwort', value: 'answer' },
            ],
            answers: [
                { id: '0xAA', name: 'Home', birthday: '01.01.1901', age: '120', gender: 'egal', answer: 'yes' },
                { id: '0xAA', name: 'Home', birthday: '01.01.1901', age: '120', gender: 'egal', answer: 'yes' },
                { id: '0xAA', name: 'Home', birthday: '01.01.1901', age: '120', gender: 'egal', answer: 'yes' },
                { id: '0xAA', name: 'Home', birthday: '01.01.1901', age: '120', gender: 'egal', answer: 'yes' },
            ],
        }
    },
}
</script>

<style scoped></style>
