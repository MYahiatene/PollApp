<script>
import { Pie } from 'vue-chartjs'

export default {
    extends: Pie,
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
