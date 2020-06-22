<template>
    <div class="container">
        <div class="large-12 medium-12 small-12 cell">
            <label>
                File
                <input id="file" ref="file" type="file" @v-on:change="handleFileUpload()" />
            </label>
            <button @v-on:click="submitFile()">Submit</button>
        </div>
    </div>
</template>

<script>
const axios = require('axios')

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

            axios
                .post('/single-file', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                })
                .then(function () {
                    console.log('SUCCESS!!')
                })
                .catch(function () {
                    console.log('FAILURE!!')
                })
        },

        handleFileUpload() {
            this.file = this.$refs.file.files[0]
        },
    },
}
</script>

<style scoped></style>
