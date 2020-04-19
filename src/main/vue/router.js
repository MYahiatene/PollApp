import VueRouter from "vue-router";
import Start from "./pages/Start";
import Article from "./pages/Article";
import Edit from "./pages/Edit";
import Create from "./pages/Create";
import Login from "./pages/Login";
import store from "./store";

const router = new VueRouter({
    mode: 'history',
    routes: [
        {
            path: '/',
            component: Start
        },
        {
            path: '/article/:id(\\d+)',
            component: Article
        },
        {
            path: '/article/:id(\\d+)/edit',
            component: Edit,
            meta: {
                requiresAuth: true
            }
        },
        {
            path: '/article/create',
            component: Create,
            meta: {
                requiresAuth: true
            }
        },
        {
            path: '/login',
            component: Login
        }
    ],

})

router.beforeEach((to, from, next) => {
    if(to.matched.some(record => record.meta.requiresAuth)) {
        if (store.getters.isAuthenticated) {
            next()
            return
        }
        next('/login')
    } else {
        next()
    }
})

export default router
