<script>
import { Bar } from 'vue-chartjs'

export default {
    extends: Bar,
    props: {
        chartdata: {
            labels: {
                type: Array,
            },
            datasets: {
                type: Array,
            },
        },
        options: {},
    },

    mounted() {
        console.log('mount')
        console.log(this.chartdata)
        this.renderChart(this.chartdata, this.options)
    },

    updated() {
        console.log('update')
    },

    watch: {
        chartdata: function handle() {
            console.log('watcher')
        },
    },
}
</script>

<style></style>
