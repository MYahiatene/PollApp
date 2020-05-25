import axios from 'axios'
// import { getters } from '../store/login'
const instance = axios.create({
    baseURL: 'http://127.0.0.1:8088/api',
    timeout: 1000,
    headers: {
        Authorization:
            'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZWN1cmUtYXBpIiwiYXVkIjoic2VjdXJlLWFwcCIsInN1YiI6InRicmV0dG1hbm4iLCJleHAiOjE1OTEyMjE4MDksInJvbCI6WyJST0xFX1BPTExfQ1JFQVRPUiJdfQ.2QQ5Pa2BSvCl8RVJguawNN0jPzT-kwyv2LR-OgXLJ7ldD9sZK2beP9xd7INsEVbo5ZH4a3wc_UpizhIBAuiajA',
    },
})
export default {
    async getPolls() {
        return await instance.get('/poll')
    },
}
