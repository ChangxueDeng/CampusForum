<script setup>
import {get, logout} from "@/net/net.js";
import {router} from "@/router/index.js";
import {useStore} from "@/store/index.js";
import {reactive, ref} from "vue";
import {useDark} from '@vueuse/core'
import {
  Back, Bell,
  ChatDotSquare, Check,
  Menu, Notification, Operation, Search, Unlock, User
} from "@element-plus/icons-vue";
import LiteCard from "@/components/LiteCard.vue";
import {ElMessage} from "element-plus";
import ColorDot from "@/components/ColorDot.vue";
import hello from '@/assets/hello.png'
import helloD from '@/assets/hello_d.png'
const isDark = useDark()
const store = useStore()
const loading = ref(true)
const searchInput = reactive({
  type: 0,
  text: ''
})
get("/api/user/info", (data)=> {
  store.user = data
  loading.value = false
})
const notification = ref([])
function getNotification() {
  get('/api/notification/list', (data)=> {
    notification.value = data
  })
}
function deleteAllNotification() {
  get('/api/notification/delete-all',()=>{
    ElMessage.success("清除所有消息成功")
    notification.value = []
  })
}
function confirmNotification(id, url) {
  get(`/api/notification/delete?id=${id}`, ()=> {
    ElMessage.success("确认消息成功")
    getNotification()
    window.open(url)
  })
}
function search() {
  if (searchInput.text.length < 1) {
    ElMessage.warning("搜索内容不能为空")
  } else {
    router.push({path: '/index/search',query:{text: searchInput.text, type: searchInput.type}})
  }
}
getNotification()
</script>

<template>
  <div class="main-content" v-loading="loading" element-loading-text="正在进入，请等待........">
    <el-container style="height: 100%" v-if="!loading">
      <el-header class="main-content-header">
        <el-image class="logo" :src="!isDark ? hello : helloD"></el-image>
        <div style="flex: 1; padding: 0 20px; text-align: center">
          <el-input style="margin-left: 220px;width: 100%; max-width: 400px;" placeholder="搜索论坛相关内容" v-model="searchInput.text" min="1" max="10">
            <template #prefix>
              <el-icon><Search/></el-icon>
            </template>
            <template #append>
              <el-select style="width: 100px" placeholder="选择分类" v-model="searchInput.type">
                <el-option v-for="(item, index) in store.forum.types" :value="index" :label="item.name">
                  <color-dot :color="item.color"></color-dot>
                  <span style="margin-left: 5px" >{{item.name}}</span>
                </el-option>
              </el-select>
            </template>
          </el-input>
          <el-button style="margin-left: 10px" type="warning" plain @click="search()">立即搜索</el-button>
        </div>
        <div class="user-info">
          <el-popover placement="bottom" :width="350" trigger="click">
            <template #reference>
              <el-badge style="margin-right: 10px" is-dot :hidden="!notification.length">
                <div class="notification">
                  <el-icon><Bell></Bell></el-icon>
                  <div style="font-size: 12px">消息</div>
                </div>
              </el-badge>
            </template>
            <el-empty :image-size="80" description="展示没有消息" v-if="!notification.length"/>
            <el-scrollbar :max-height="500" v-else>
              <lite-card v-for="item in notification" class="notification-item"
                         @click="confirmNotification(item.id, item.url)">
                <div>
                  <el-tag :type="item.type">消息</el-tag> &nbsp;
                  <span style="font-weight: bold">{{item.title}}</span>
                </div>
                <el-divider style="margin: 5px 0"></el-divider>
                <div style="font-size: 13px; color: grey">
                  {{item.content}}
                </div>
              </lite-card>
              <div style="margin-top: 10px">
                <el-button size="small" type="info" :icon="Check" @click="deleteAllNotification"
                           style="width: 100%" plain>清除全部消息</el-button>
              </div>
            </el-scrollbar>
          </el-popover>
          <div class="profile">
            <div>{{store.user.username}}</div>
            <div>{{store.user.email}}</div>
          </div>
          <el-dropdown>
            <el-avatar :src="store.avatarUrl"></el-avatar>
            <template #dropdown>
              <el-dropdown-item @click="router.push('/index/user-setting')">
                <el-icon><Operation/></el-icon>
                个人设置
              </el-dropdown-item>
              <el-dropdown-item divided @click="logout(()=>{router.push('/')})">
                <el-icon><Back/></el-icon>
                退出登录
              </el-dropdown-item>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <el-aside width="150px" class="aside">
          <el-scrollbar style="height: calc(100vh - 55px);">
            <div style="display: flex; flex-direction: column; justify-content: center;
                  min-height: calc(100vh - 55px);">
              <el-tooltip content="帖子广场" placement="right">
                <el-button class="menu-button"
                           @click="store.forumActivate(1); router.push('/index') "
                           :style="{border: store.page.forum === 1 ?`1px solid #409EFF` : '',
                         background : store.page.forum === 1 ? 'var(--el-button-active-bg-color)' : ''}">
                  <template #icon>
                    <el-icon size="25" :color="store.page.forum === 1 ? '#409EFF' : ''"><ChatDotSquare/></el-icon>
                  </template>
                </el-button>
              </el-tooltip>
              <el-tooltip content="个人空间" placement="right">
                <el-button class="menu-button"
                           @click="store.forumActivate(2); router.push(`/index/space/${store.user.id}`)"
                           :style="{border: store.page.forum === 2 ?`1px solid #409EFF` : '',
                         background : store.page.forum === 2 ? 'var(--el-button-active-bg-color)' : ''}">
                  <template #icon>
                    <el-icon size="25" :color="store.page.forum === 2 ? '#409EFF' : ''"><Notification/></el-icon>
                  </template>
                </el-button>
              </el-tooltip>
              <el-tooltip content="信息设置" placement="right">
                <el-button class="menu-button"
                           @click="store.forumActivate(3);router.push('/index/user-setting');"
                           :style="{border: store.page.forum === 3 ?`1px solid #409EFF` : '',
                         background : store.page.forum === 3 ? 'var(--el-button-active-bg-color)' : ''}">
                  <template #icon>
                    <el-icon size="25" :color="store.page.forum === 3 ? '#409EFF' : ''"><User/></el-icon>
                  </template>
                </el-button>
              </el-tooltip>
              <el-tooltip content="安全设置" placement="right">
                <el-button class="menu-button"
                           @click="store.forumActivate(4);router.push('/index/privacy-setting')"
                           :style="{border: store.page.forum === 4 ?`1px solid #409EFF` : '',
                         background : store.page.forum === 4 ? 'var(--el-button-active-bg-color)' : ''}">
                  <template #icon>
                    <el-icon size="25" :color="store.page.forum === 4 ? '#409EFF' : ''"><Unlock/></el-icon>
                  </template>
                </el-button>
              </el-tooltip>
              <el-tooltip content="管理面板" placement="right" v-if="store.user.role === 'admin'">
                <el-button class="menu-button"
                           @click="router.push('/admin')">
                  <template #icon>
                    <el-icon><Menu/></el-icon>
                  </template>
                </el-button>
              </el-tooltip>
            </div>
          </el-scrollbar>
        </el-aside>
        <el-main class="main-content-page">
          <el-scrollbar style="height: calc(100vh - 55px);">
              <router-view v-slot="{Component}">
                <transition mode="out-in" name="el-fade-in-linear">
                  <component :is="Component" style="height: 100%"></component>
                </transition>
              </router-view>
          </el-scrollbar>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="less" scoped>
.menu-button{
  margin: 5px 5px 5px 15px;
  width: 50px;
  height: 50px;
  border-radius: 10px;
  //border: 1px solid var(--el-color-primary);
}
.notification-item{
  transition: .3s;
  &:hover{
    cursor: pointer;
    opacity: 0.7;
  }
}
.notification{
  font-size: 22px;
  line-height: 14px;
  text-align: center;
  transition: color 0.3s;

  &:hover{
    cursor: pointer;
    color: grey;
  }
}
.main-content {
  height: 100vh;
  width: 100vw;
}
.main-content-header{
  border-bottom: solid 1px var(--el-border-color);
  height: 55px;
  display: flex;
  align-items: center;
  box-sizing: border-box;
  .logo{
    height: 50px;
    border-radius: 5px;
    width: 180px;
    background-color: #181818;
  }
  .user-info{
    display: flex;
    justify-content: flex-end;
    align-items: center;

    .el-avatar:hover{
      cursor: pointer;
    }
    .profile{
      text-align: right;
      margin-right: 20px;
      :first-child{
        font-size: 18px;
        font-weight: bold;
        line-height: 20px;
      }
      :last-child{
        font-size: 10px;
        color: gray;
      }
    }
  }
}
.dark .main-content-page{
  background-color: #242628;

}
.dark .aside{
  background-color: #242628;

}

.aside{
  background-color: #f7f8fa;;
}
.main-content-page{
  padding: 0;
  background-color: #f7f8fa;
}
</style>