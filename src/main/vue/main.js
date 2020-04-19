import Vue from 'vue'
import App from './App.vue'
import VueRouter from "vue-router";
import Vuex from "vuex";
import router from "./router";
import store from "./store";
import {BootstrapVue} from "bootstrap-vue";

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

Vue.config.productionTip = false

Vue.use(BootstrapVue)
Vue.use(VueRouter)
Vue.use(Vuex)

new Vue({
  render: h => h(App),
  router,
  store
}).$mount('#app')
