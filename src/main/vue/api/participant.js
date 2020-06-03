import axios from 'axios'
const instance = axios.create({
    baseURL: 'http://127.0.0.1:8088/api/',
    timeout: 1000,
    headers: {
        Authorization:
            'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZWN1cmUtYXBpIiwiYXVkIjoic2VjdXJlLWFwcCIsInN1YiI6InRicmV0dG1hbm4iLCJleHAiOjE1OTIwMzMwMzQsInJvbCI6WyJST0xFX1BPTExfQ1JFQVRPUiJdfQ.If3LkXprraScICaQ5bmmef8HhOXKRG_0kWVuM42mWBZ3Sfx-SvhmuHRWkdmaOwNwAPQgl-5FOw_QJg1hH3hluQ',
    },
})
export default {
    /**
     * Calls getParticipant in PollController and returns a promise of the saved poll.
     * @returns {Promise<AxiosResponse<any>>}
     */
    async getPoll() {
        return await instance.get('/participant')
    },
    saveAnswers(answerObj) {
        const answerCmd = answerObj
        return instance.post('/poll/pollId:1/addanswer', answerCmd)
    },
}
