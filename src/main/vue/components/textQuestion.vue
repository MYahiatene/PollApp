<template>

<!--    this widget includes the necessary controls to create a textQuestion-->
    <v-container>
        <v-form v-model="valid">
            <v-row no-gutters>
                <v-switch v-model="enterSwitch" label="Enter erlauben"></v-switch>
            </v-row>
            <v-row no-gutters>
                <v-switch v-model="minCharSwitch" label="Zeichenanzahl nach unten beschränken"></v-switch>
            </v-row>
<!--            textField to specify the lowest possible number of chars-->
            <v-row v-if="minCharSwitch">
                <v-col md="5">
                    <v-text-field
                        single-line
                        type="number"
                        min="0"
                        value="5"
                        prefix="mindestens"
                        suffix=" Zeichen gefordert"
                        :rules="charRules"
                    ></v-text-field>
                </v-col>
            </v-row>
            <v-row no-gutters>
                <v-switch v-model="maxCharSwitch" label="Zeichenanzahl nach oben beschränken"></v-switch>
            </v-row>
            <!--            textField to specify the highest possible number of chars-->
            <v-row v-if="maxCharSwitch">
                <v-col md="5">
                    <v-text-field
                        single-line
                        type="number"
                        min="1"
                        value="100"
                        prefix="maximal"
                        suffix=" Zeichen erlaubt"
                        :rules="charRules"
                    ></v-text-field>
                </v-col>
            </v-row>
        </v-form>
    </v-container>
</template>

<script>
export default {
    name: 'textQuestion',
    data() {
        return {
            enterSwitch: false,
            minCharSwitch: false,
            maxCharSwitch: false,
            valid: false,
            // rules that assert that the input in the textField musst be positive (there can't be a negative number of chars on an answer
            charRules: [(v) => v >= 0 || 'Die Zahl muss positiv sein!'],
        }
    },
}
</script>

<style scoped></style>
