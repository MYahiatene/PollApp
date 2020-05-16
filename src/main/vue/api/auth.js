import axios from 'axios'
export default {
    login(username, password) {
        const credentials = new URLSearchParams()
        credentials.append('username', username)
        credentials.append('password', password)
        return axios.post('http://localhost:8088/api/authenticate', credentials)
    },
}
