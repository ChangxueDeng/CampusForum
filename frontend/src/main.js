import { createApp } from 'vue'
import App from './App.vue'
import {router} from "@/router/index.js";
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import axios from "axios";
import {createPinia} from "pinia";
import '@vueup/vue-quill/dist/vue-quill.snow.css';

const app = createApp(App)
const pinia = createPinia()
axios.defaults.baseURL = 'http://localhost:8081'
//axios.defaults.baseURL = 'https://dengchangxue.top:8081'
app.use(pinia)
app.use(router)
app.mount('#app')
