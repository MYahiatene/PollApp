<template>
  <div id="app">
    <HelloWorld msg="Welcome to Your Vue.js App!"/>
    <HelloWorld v-bind:msg = backendData />
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import axios from 'axios'

export default {
  name: 'App',
  data() {
    return {
      backendData: ""
    }
  },
  components: {
    HelloWorld
  },
  created() {
    axios.get("http://localhost:8088/hello-world")
      .then(response => this.backendData = response.data)
      .catch(e => console.log(e));
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
