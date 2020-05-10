import axios from 'axios';
export default {
    getPoll() {
        return axios.get('/api/participant')
    }
}

