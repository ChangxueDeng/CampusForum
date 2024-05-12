import {createRouter, createWebHistory} from "vue-router";
import {unAuthorized} from "@/net/net.js";
import {useStore} from "@/store/index.js";


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
                path:'',
                name: 'forum',
                component: ()=> import("@/views/forum/Forum.vue"),
                children: [
                    {
                        path: '',
                        name: 'topic-list',
                        component: () => import ('@/views/forum/TopicListPage.vue')
                    },
                    {
                        path: 'topic-detail/:tid',
                        name: 'topic-detail',
                        component: ()=> import('@/views/forum/TopicDetailPage.vue')
                    },
                    {
                        path: 'space/:id',
                        name: 'space',
                        component: () => import('@/views/forum/SpacePage.vue')
                    },
                    {
                        path: 'search',
                        name: 'search',
                        component: ()=> import('@/views/forum/SearchListPage.vue')
                    }
                ]
            },
            {
                path: 'user-setting',
                name: 'user-setting',
                component: ()=> import('@/views/index/UserSettingPage.vue')
            },
            {
                path: 'privacy-setting',
                name: 'privacy-setting',
                component: ()=> import('@/views/index/PrivacySetting.vue')
            },



        ]
    },
    {
        path: '/admin',
        name: 'admin',
        component: ()=> import('@/views/AdminPage.vue'),
        children: [
            {
                path: '',
                name: 'overview',
                component: ()=> import('@/views/admin/OverviewPage.vue')
            },
            {
                path: 'topic-manage',
                name: 'topic-manage',
                component: ()=> import('@/views/admin/TopicManagePage.vue')
            },
            {
                path: 'user-manage',
                name: 'user-manage',
                component: ()=> import('@/views/admin/UserManagePage.vue')
            },
            {
                path: 'notice-manage',
                name: 'notice-manage',
                component: ()=> import('@/views/admin/NoticeManagePage.vue')
            },
            {
                path: 'comment-manage',
                name: 'comment-manage',
                component: () => import('@/views/admin/CommentManagePage.vue')
            },
            {
                path: 'announcement-manage',
                name: 'announcement-manage',
                component: () => import('@/views/admin/AnnouncementManagePage.vue')
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: routes,
})


router.beforeEach((to, from, next) =>{
    const isNotAuthorized = unAuthorized();
    const store = useStore()
    if(to.name.startsWith('welcome-') && !isNotAuthorized){
        next('/index')
    }else if(to.fullPath.startsWith('/index') && isNotAuthorized){
        next('/')
    }else if (to.fullPath.startsWith('/admin') && store.user.role !== 'admin'){
        next('/')
    }else {
    if (to.name === 'topic-list' || to.name === 'topic-detail') {
        store.forumActivate(1)
    }else if (to.name === 'space') {
        store.forumActivate(2)
    } else if (to.name === 'user-setting') {
        store.forumActivate(3)
    } else if (to.name === 'privacy-setting') {
        store.forumActivate(4)
    }
    next()
    }
} )


export {router}