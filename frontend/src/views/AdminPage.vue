<script setup>
import { ref } from 'vue'

import {router} from "@/router/index.js";
import {useStore} from "@/store/index.js";
import {ArrowLeft, Back, Operation} from "@element-plus/icons-vue";
import {get, logout} from "@/net/net.js";
const store = useStore()
const loading = ref(false)
get("/api/user/info", (data)=> {
  store.user = data
  loading.value = true
})
get('/api/admin/overview', data => {
  store.admin = data
})
get('api/forum/types', data => {
  const array = []
  array.push({name: '全部', id: 0, color: 'linear-gradient(45deg, white, red, orange, gold, green, blue)'})
  data.forEach(d => array.push(d))
  store.forum.types = array
})
function jump() {
  switch (store.page.admin) {
    case 'overview':
      router.push('/admin')
      break
    case 'topic':
      router.push('/admin/topic-manage')
      break
    case 'user':
      router.push('/admin/user-manage')
      break
    case 'notice':
      router.push('/admin/notice-manage')
      break
    case 'comment':
      router.push('/admin/comment-manage')
      break
    case 'announcement':
      router.push('/admin/announcement-manage')
      break
  }
}

</script>

<template>
    <div class="main-page" v-loading="!loading">
      <el-container>
        <el-header class="header">
          <div style="margin-top: 10px; margin-right: 5px">
            <el-button round @click="router.push('/index')" :icon="ArrowLeft">返回论坛</el-button>
          </div>
          <el-divider direction="vertical" style="margin-top: 2px; height: 50px"></el-divider>
          <div style="flex: 1; margin-right: 10px">
            <el-tabs tab-position="top" :stretch="true" style="height: 100%;
                   flex: 1" class="demo-tabs" v-model="store.page.admin" @tab-change="jump();">
              <el-tab-pane label="概括" name="overview"></el-tab-pane>
              <el-tab-pane  label="帖子管理" name="topic"></el-tab-pane>
              <el-tab-pane label="用户管理" name="user"></el-tab-pane>
              <el-tab-pane label="通知管理" name="notice"></el-tab-pane>
              <el-tab-pane label="评论管理" name="comment"></el-tab-pane>
              <el-tab-pane label="公告管理" name="announcement"></el-tab-pane>
            </el-tabs>
          </div>
          <div class="user-info">
            <div style="text-align: right">
              <div style="font-size: 15px; font-weight: bold">{{store.user.username}}</div>
              <div style="font-size: 10px; color: grey">{{store.user.email}}</div>
            </div>
            <el-dropdown style="margin-left: 5px">
              <el-avatar :src="store?.avatarUrl"></el-avatar>
              <template #dropdown>
                <el-dropdown-item @click="logout(()=>{router.push('/')})">
                  <el-icon><Back/></el-icon>
                  退出登录
                </el-dropdown-item>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-container>
          <el-main class="main">
            <div>
                <router-view v-slot="{Component}">
                  <transition mode="out-in" name="el-fade-in-linear">
                    <keep-alive>
                      <component :is="Component" style="height: 100%"></component>
                    </keep-alive>
                  </transition>
                </router-view>
            </div>
          </el-main>
        </el-container>
      </el-container>
    </div>
</template>

<style lang="less" scoped>
.main-page{
  height: 100vh;
  width: 100vw;
  display: flex;
  overflow: hidden;
}
.header{
  display: flex;
  background-color: #F5F7FA;
  border-radius: 5px;
  border: 1px solid #E4E7ED;
  height: 55px;
  justify-content: center;
  .user-info{
    margin-top: 10px;
    display: flex;
    justify-content: flex-end;
  }
}
.main{
  margin-top: 5px;
  background-color: #FAFCFF;
}
.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}

.el-tabs--right .el-tabs__content,
.el-tabs--left .el-tabs__content {
  height: 100%;
}
</style>