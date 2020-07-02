<template>
    <v-card>
        <v-btn icon nuxt :to="actionLink">
            <v-icon color="primary">
                Export
            </v-icon>
        </v-btn>
    </v-card>
</template>

<script>
export default {
    name: 'ExportTestSite',
}
</script>

<style scoped></style>
