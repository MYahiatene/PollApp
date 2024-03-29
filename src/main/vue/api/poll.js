import axios from 'axios'
// import PollEditing from "../pages/PollEditing";
const instance = axios.create({
    baseURL: 'http://127.0.0.1:8088/api/',
    timeout: 1000,
    headers: {
        Authorization:
            'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZWN1cmUtYXBpIiwiYXVkIjoic2VjdXJlLWFwcCIsInN1YiI6InRicmV0dG1hbm4iLCJleHAiOjE1OTExMTAzMzQsInJvbCI6WyJST0xFX1BPTExfQ1JFQVRPUiJdfQ.Q6A2ST5I5Ix8_8jfsgxc3ZQq9GG7i88w_bJPlfEYA-QiAavpUhPbjFoUQWd9vZ93Xqzvm4oCw23bJ1NGtp2ucw',
    },
})

export default {
    save(poll) {
        /* const pollCmd = {
            pollname: poll.pollname,
            activatedAt: poll.activatedAt,
            visibility: poll.visibility,
            categoryChange: poll.categoryChange,
        } */
        // more attributes to follow here
        return instance.post('/PollCreation', poll) // is this url right?, because when you save, you
        // get redirected to a new Page (PollEditing)
    },
}
