<script>
// This compoent gets options and chartdata as props and displayes a barChart from it
import { Bar } from 'vue-chartjs'

export default {
    extends: Bar,
    props: {
        chartdata: {
            labels: {},
            datasets: {},
            backgroundColor: {},
        },
        options: {},
    },
    mounted() {
        // // console.log('mount')
        // // console.log(this.chartdata)
        this.renderChart(this.chartdata, this.options)
    },

    updated() {
        // // console.log('update')
    },
}
</script>

<style></style>
