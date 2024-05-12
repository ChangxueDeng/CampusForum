import {defineStore} from "pinia";
import axios from "axios";
export const useStore = defineStore('general', {
    state: () => {
        return{
            user:{
                id: -1,
                username:'',
                email: '',
                role: '',
                registerTime:'',
                avatar: '',
            },
            forum:{
                types: [],
                announcements:[]
            },
            admin:{
                topicCount: 0,
                topicCountByType: [],
                userCount: 0,
                commentCount: 0,
                notificationCount: 0,
                announcementCount: 0,
                announcement: null,
            },
            page:{
                forum: 1,
                admin: 'overview',
            }
        }
    },
    getters: {
        avatarUrl() {
            if(this.user.avatar) {
                return `${axios.defaults.baseURL}/images${this.user.avatar}`
            } else {
                return "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
            }
        }
    },
    actions: {
        findTyById(id) {
            for (let type of this.forum.types) {
                if (type.id === id) {
                    return type
                }
            }
        },
        avatarUserUrl(avatar) {
            if(avatar) {
                return `${axios.defaults.baseURL}/images${avatar}`
            } else {
                return "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
            }
        },
        forumActivate(page) {
            this.page.forum = page
        },
        adminActivate(page) {
            this.page.admin = page
        }
    }

})