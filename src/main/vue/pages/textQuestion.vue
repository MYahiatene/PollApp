<template>
    <v-card>
        <v-container>
            <v-form v-model="valid">
                <v-row>
                    <v-switch v-model="enterSwitch" label="Enter erlauben"></v-switch>
                </v-row>
                <v-row>
                    <v-switch v-model="charSwitch" label="Zeichenanzahl beschränken"></v-switch>
                </v-row>
                <v-row v-if="charSwitch">
                    <v-col cols="10" md="4">
                        <v-text-field
                            single-line
                            type="number"
                            min="1"
                            value="100"
                            style="width: 170px;"
                            suffix=" Zeichen erlaubt"
                            :rules="charRules"
                        ></v-text-field>
                    </v-col>
                </v-row>
            </v-form>
        </v-container>
    </v-card>
</template>

<script>
export default {
    name: 'textQuestion',
    data() {
        return {
            enterSwitch: false,
            charSwitch: false,
            valid: false,
            charRules: [(v) => v > 1 || 'Die Zahl muss positiv sein!'],
        }
    },
}
</script>

<style scoped></style>
