<script>
import { mapActions, mapGetters } from 'vuex'

export default {
    name: 'Login',
    data() {
        return {
            auth: {
                username: '',
                password: '',
            },
        }
    },
    computed: {
        ...mapGetters({
            authenticated: 'isAuthenticated',
        }),
    },
    methods: {
        ...mapActions(['requestToken']),
        login() {
            this.requestToken(this.auth).then(() => this.$router.push('/'))
        },
    },
}
</script>
