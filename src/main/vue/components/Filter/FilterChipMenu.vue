<template>
    <v-menu offset-y>
        <template v-slot:activator="{ on }">
            <v-btn :color="color" elevation="0" style="box-shadow: none;" v-on="on">
                {{ selected }}
            </v-btn>
        </template>
        <v-list>
            <v-list-item v-for="(item, index) in items" :key="index" @click="setChoice(item)">
                <v-list-item-title>
                    {{ item }}
                </v-list-item-title>
            </v-list-item>
        </v-list>
    </v-menu>
</template>

<script>
export default {
    name: 'FilterChipMenu',
    data() {
        return {
            selected: '',
        }
    },
    props: {
        color: {
            type: String,
        },
        items: {
            type: Array,
        },
    },
    methods: {
        setChoice(item) {
            this.selected = item
            this.$emit('choice-updated', item)
        },
    },
}
</script>

<style scoped></style>
