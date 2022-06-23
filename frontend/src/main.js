import Vue from "vue";
import VueRouter from "vue-router";
import App from "@/App.vue";
import FreemarkerViewer from "@/components/FreemarkerViewer.vue";
import EditorConfiguration from "@/components/EditorConfiguration.vue";

Vue.config.productionTip = false;
Vue.use(VueRouter);

const routes = [
  {
    name: 'editor',
    path: '/',
    component: FreemarkerViewer
  },
  {
    name: 'config',
    path: '/config',
    component: EditorConfiguration
  },
]

const router = new VueRouter({
  mode: 'history',
  routes
})

new Vue({
  router,
  render: (h) => h(App)
}).$mount("#app");
