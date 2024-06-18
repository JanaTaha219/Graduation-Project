<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>
<script>
import logIn from "./views/logIn.vue";

export default {
  name: "App",

  components: { logIn },
};
</script>
