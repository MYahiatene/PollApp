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
        this.renderChart(this.chartdata, this.options)
    },
}
</script>

<style></style>
