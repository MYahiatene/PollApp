<template>
    <div class="container">
        <h1 v-if="error.statusCode === 404">Die Seite wurde leider nicht gefunden.</h1>
        <v-img
            v-if="error.statusCode === 404"
            src="https://cdn.pixabay.com/photo/2020/06/22/12/38/cat-5329041_960_720.jpg"
        ></v-img>
        <h1 v-else>Das hat leider nicht funktioniert</h1>
        <nuxt-link to="/">Zurück zur Hauptseite</nuxt-link>
    </div>
</template>
<script>
export default {
    props: ['error'],
    layout: 'blog',
}
</script>
