<script>
// This compoent gets options and chartdata as props and displayes a pieChart from it
import { Pie } from 'vue-chartjs'

export default {
    extends: Pie,
    props: {
        chartdata: {
            labels: {},
            datasets: {},
        },
        options: {},
    },
    mounted() {
        this.renderChart(this.chartdata, this.options)
    },
}
</script>

<style></style>
