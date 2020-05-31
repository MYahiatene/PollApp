import axios from 'axios'
// import { state } from '../store/login'
const instance = axios.create({
    baseURL: 'http://127.0.0.1:8088/api/evaluation',
    timeout: 1000,
    headers: {
        Authorization:
            'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZWN1cmUtYXBpIiwiYXVkIjoic2VjdXJlLWFwcCIsInN1YiI6InRicmV0dG1hbm4iLCJleHAiOjE1OTEyMjE4MDksInJvbCI6WyJST0xFX1BPTExfQ1JFQVRPUiJdfQ.2QQ5Pa2BSvCl8RVJguawNN0jPzT-kwyv2LR-OgXLJ7ldD9sZK2beP9xd7INsEVbo5ZH4a3wc_UpizhIBAuiajA',
    },
})
export default {
    async getInitialDiagrams() {
        console.log(instance.defaults.headers)
        return await instance.get('/initialDiagrams')
    },
    async sendFilter(filterList) {
        console.log(filterList)
        console.log(instance.defaults.headers)
        await instance.post('/generateDiagram', filterList)
        // return await instance.get('/getDiagram')
    },
}
