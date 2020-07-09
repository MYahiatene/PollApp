<template>
    <div class="container">
        <v-card class="pa-6">
            <h1>Das hat leider nicht funktioniert.</h1>
            <p></p>
            <p>Statuscode: {{ error.statusCode }}</p>
            <p v-if="error.statusCode === 400">Ihr Browser hat eine kaputte Anfrage gesendet.</p>
            <p v-if="error.statusCode === 401">Leider sind Sie nicht authorisiert.</p>
            <p v-if="error.statusCode === 403">Forbidden</p>
            <p v-if="error.statusCode === 404">Toto, I've a feeling we're not in Kansas anymore.</p>
            <p v-if="error.statusCode === 418">Der Server ist leider eine Teekanne.</p>
            <p v-if="error.statusCode === 500">Interner Fehler. Wir arbeiten daran.</p>
            <p v-if="error.statusCode === 502">Bad Gateway</p>
            <p v-if="error.statusCode === 503">Service nicht erreichbar. Wir arbeiten daran.</p>
            <p v-if="error.statusCode === 504">Gateway Timeout</p>
            <p>Lächel mal :)</p>
            <v-btn to="/">Zurück zur Hauptseite</v-btn>
            <v-btn to="/help">Hilfe-Seite</v-btn>
        </v-card>
    </div>
</template>
<script>
export default {
    props: ['error'],
    layout: 'blog',
}
</script>
