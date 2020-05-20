<template>
    <div>
        <AuthGate v-if="isAuthenticated !== true"></AuthGate>
        <v-layout v-else>
            <v-flex class="text-center">
                <v-card class="pa-5">
                    <h1>Administrationsseite</h1>
                </v-card>
            </v-flex>
        </v-layout>
    </div>
</template>
