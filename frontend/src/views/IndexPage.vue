<script setup>
import {get, logout} from "@/net/net.js";
import {router} from "@/router/index.js";
import {useStore} from "@/store/index.js";
import {reactive, ref} from "vue";
import {
  Back,
  ChatDotSquare,
  Location, Lock,
  Lollipop, Message,
  Notification,
  OfficeBuilding, Operation,
  Position, Search, Setting,
  Stopwatch, Unlock, User
} from "@element-plus/icons-vue";

const store = useStore()

const loading = ref(true)

const searchInput = reactive({
  type: '1',
  text: ''
})
get("/api/user/info", (data)=> {
  store.user = data
  loading.value = false
})

</script>

<template>
  <div class="main-content" v-loading="loading" element-loading-text="正在进入，请等待........">
    <el-container style="height: 100%" v-if="!loading">
      <el-header class="main-content-header">
        <el-image class="logo" src="https://element-plus.org/images/element-plus-logo.svg"></el-image>
        <div style="flex: 1; padding: 0 20px; text-align: center">
          <el-input style="width: 100%; max-width: 400px;" placeholder="搜索论坛相关内容" v-model="searchInput.text">
            <template #prefix>
              <el-icon><Search/></el-icon>
            </template>
            <template #append>
              <el-select style="width: 100px" placeholder="选择分类" v-model="searchInput.type">
                <el-option value="1" label="帖子广场"></el-option>
                <el-option value="2" label="失物招领"></el-option>
                <el-option value="3" label="校园活动"></el-option>
                <el-option value="4" label="表白墙"></el-option>
              </el-select>
            </template>
          </el-input>
        </div>
        <div class="user-info">
          <div class="profile">
            <div>{{store.user.username}}</div>
            <div>{{store.user.email}}</div>
          </div>
          <el-dropdown>
            <el-avatar :src="store.avatarUrl"></el-avatar>
            <template #dropdown>
              <el-dropdown-item>
                <el-icon><Operation/></el-icon>
                个人设置
              </el-dropdown-item>
              <el-dropdown-item>
                <el-icon><Message/></el-icon>
                消息列表
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
        <el-aside width="220px">
          <el-scrollbar style="height: calc(100vh - 55px);">
            <el-menu style="min-height: calc(100vh - 55px);" router :default-active="$route.path">
              <el-sub-menu index="1">
                <template #title>
                  <el-icon><Location></Location></el-icon>
                  <span>校园论坛</span>
                </template>
                <el-menu-item index="/index/">
                  <template #title>
                    <el-icon><ChatDotSquare/></el-icon>
                    帖子广场
                  </template>
                </el-menu-item>
                <el-menu-item >
                  <template #title>
                    <el-icon><Stopwatch /></el-icon>
                    失物招领
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Notification /></el-icon>
                    校园活动
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Lollipop /></el-icon>
                    表白墙
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><OfficeBuilding /></el-icon>
                    考研培训
                    <el-tag style="margin-left: 10px" size="small">合作机构</el-tag>
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="2">
                <template #title>
                  <el-icon><Position></Position></el-icon>
                  <span>探索与发现</span>
                </template>
                <el-menu-item index="2-1">
                  <template #title>
                    <el-icon><ChatDotSquare/></el-icon>
                    文学交流
                  </template>
                </el-menu-item>
                <el-menu-item >
                  <template #title>
                    <el-icon><Stopwatch /></el-icon>
                    编程交友
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Notification /></el-icon>
                    好物安利
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Lollipop /></el-icon>
                    资源分享
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="3">
                <template #title>
                  <el-icon><Operation></Operation></el-icon>
                  <span>个人设置</span>
                </template>
                <el-menu-item index="/index/user-setting">
                  <template #title>
                    <el-icon><User/></el-icon>
                    信息设置
                  </template>
                </el-menu-item>
                <el-menu-item index="/index/privacy-setting">
                  <template #title>
                    <el-icon><Unlock/></el-icon>
                    安全设置
                  </template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
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
    height: 32px;
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
.main-content-page{
  padding: 0;
  background-color: #f7f8fa;
}
</style>