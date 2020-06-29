<template>
    <div class="container">
        <div class="large-12 medium-12 small-12 cell">
            <label>
                CSV Datei mit E-Mail Adressen hochladen:
                <input id="file" ref="file" type="file" @v-on:change="handleFileUpload()" />
            </label>
            <button @v-on:click="submitFile()">Submit</button>
        </div>
        <v-container>
            <template>
                <v-data-table
                    :headers="headers"
                    :items="prepareItems"
                    :search="search"
                    :custom-filter="filterOnlyCapsText"
                    class="elevation-1"
                    :footer-props="footerProps"
                >
                    <template v-slot:item.status="{ item }">
                        <v-icon @click="activatePoll(item)">
                            {{ item.statusIcon }}
                        </v-icon>
                    </template>
                    <template v-slot:item.action="{ item }">
                        <v-icon @click="itemAction(item)">
                            {{ item.actionIcon }}
                        </v-icon>
                    </template>
                    <template v-slot:item.link="{ item }">
                        <v-icon v-if="item.pollStatus === 1" @click="setLink(item)">
                            mdi-link-variant
                        </v-icon>
                    </template>
                </v-data-table>
            </template>
        </v-container>
    </div>
</template>

<script>
// const axios = require('axios')

export default {
    data() {
        return {
            file: '',
        }
    },

    methods: {
        submitFile() {
            const formData = new FormData()

            formData.append('file', this.file)

            /* axios
                .post('/sendCsv', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                })
                .then(function () {
                    console.log('SUCCESS!!')
                })
                .catch(function () {
                    console.log('FAILURE!!')
                }) */
            this.$axios.post('/sendCsv', this.file).catch()
        },
        handleFileUpload() {
            this.file = this.$refs.file.files[0]
        },
    },
}
</script>

<style scoped></style>
