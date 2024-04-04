import {createRouter, createWebHistory} from "vue-router";
import {unAuthorized} from "@/net/net.js";

const routes = [
    {
        path: '/',
        name: 'welcome',
        component: ()=> import('@/views/WelcomePage.vue'),
        children:[
            {
                path: '/',
                name: 'welcome-login',
                component: ()=>import('@/views/welcome/LoginPage.vue')
            },
            {
                path: '/register',
                name: 'welcome-register',
                component: () =>import('@/views/welcome/RegisterPage.vue')
            },
            {
                path: '/forget',
                name: 'welcome-forget',
                component: ()=>import('@/views/welcome/ForgetPage.vue')
            }
        ]
    },
    {
        path: '/index',
        name: 'index',
        component: ()=> import('@/views/IndexPage.vue'),
        children: [
            {
                path: 'user-setting',
                name: 'user-setting',
                component: ()=> import('@/views/index/UserSettingPage.vue')
            },
            {
                path: 'privacy-setting',
                name: 'privacy-setting',
                component: ()=> import('@/views/index/PrivacySetting.vue')
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: routes,
})


router.beforeEach((to, from, next) =>{
    const isAuthorized = unAuthorized();
    if(to.name.startsWith('welcome-') && !isAuthorized){
        next('/index')
    }else if(to.fullPath.startsWith('/index') && isAuthorized){
        next('/')
    }else {
        next()
    }
} )


export {router}