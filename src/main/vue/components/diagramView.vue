<!DOCTYPE html>

<template>
    <p>
        {{ data }}
    </p>
</template>

<script>
import { Pie } from 'vue-chartjs'

export default {
    name: 'diagramView',
    props: ['data'],
    extends: Pie,
    data: () => ({
        chartdata: {
            labels: ['Ja', 'Nein', 'Enthaltung'],
            datasets: [
                {
                    label: 'Ist das eine Frage?',
                    backgroundColor: '#111111',
                    data: [18, 5, 7],
                },
                {
                    label: 'Ist das eine bessere Frage?',
                    backgroundColor: '#444444',
                    data: [6, 16, 8],
                },
                {
                    label: 'Ist das eine schlechtere Frage?',
                    backgroundColor: '#888888',
                    data: [3, 3, 14],
                },
            ],
        },

        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                yAxes: [
                    {
                        ticks: {
                            beginAtZero: true,
                        },
                    },
                ],
            },
        },
    }),

    // mounted() {
    //     this.renderChart(this.data, this.options)
    // },
}
</script>

<style scoped></style>
