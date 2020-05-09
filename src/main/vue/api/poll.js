import axios from 'axios';
import PollEditing from "../pages/PollEditing";

export default {
    save(poll) {
        let pollCmd = {title: poll.title, activatedAt: poll.activatedAt}; //more attributes to follow here
        return axios.post('/api/PollCreation', pollCmd); // is this url right?, because when you save, you
        //get redirected to a new Page (PollEditing)
    },
}
