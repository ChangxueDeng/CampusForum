<script setup>

import Card from "@/components/Card.vue";
import LiteCard from "@/components/LiteCard.vue";
import {
  ArrowLeft,
  CircleCheck,
  Clock,
  CloseBold,
  Connection,
  Discount,
  Female, FolderOpened,
  Male,
  Select,
  Star,
  WindPower
} from "@element-plus/icons-vue";
import TopicTag from "@/components/TopicTag.vue";
import {reactive, ref, watch} from "vue";
import {router} from "@/router/index.js";
import {get} from "@/net/net.js"
import {onBeforeRouteUpdate, useRoute} from "vue-router";
import {useStore} from "@/store/index.js";
import TopicCollectList from "@/components/TopicCollectList.vue";
import FollowList from "@/components/FollowList.vue";
import {ElMessage, ElMessageBox} from "element-plus";
const route = useRoute()

let id = Number.parseInt(route.params.id)
const store = useStore()
const space = reactive({
  id: 0,
  gender:0,
  username: "",
  desc: "",
  avatar: "",
  like: 0,
  follow: 0,
  fans: 0,
  loading: false,
  followed: false,
  collectShow: false,
  followerShow: false,
  fansShow: false,
})
const topics = reactive({
  list: [],
  page: 1,
  end: false, //是否到达最后
})
function getSpace(id) {
  get(  `api/forum/space?id=${id}`, (data) => {
    space.id = data.uid
    space.username = data.username
    space.desc = data.desc
    space.avatar = data.avatar
    space.like = data.like
    space.follow = data.follow
    space.fans = data.fans
    space.gender = data.gender
    space.followed = data.followed
    space.loading = true
  })
}
function getSpaceTopics() {
  if (topics.end) return
  get(`api/forum/space-topics?id=${id}&page=${topics.page}`, data => {
    if (data) {
      data.forEach (t => topics.list.push(t))
      topics.page++
    }
    if (!data || data.length < 10) {
      topics.end = true
    }
  })
}
function following(targetId, state) {
  get(`api/forum/follow-user?targetId=${targetId}&state=${state}`,()=> {
    ElMessage.success(state ? '关注成功' : '取消关注成功')
    space.followed = state
  })
}
function clickTopic(id, ban) {
  if(ban) {
    ElMessageBox.alert('该主题已被封禁', '提示')
  } else {
    router.push({path: '/index/topic-detail/' + id, query:{from : 'space'}})
  }
}
getSpace(id)

onBeforeRouteUpdate((to, from, next)=> {
  console.log(`to ${to.params.id}; from ${from.params.id}`)
  if (to.name === 'space' && to.params.id !== from.params.id) {
    console.log(`内部to ${to.params.id}; from ${from.params.id}`)
    getSpace(to.params.id)
    topics.list = []
    topics.page = 1
    topics.end = false
    id = Number.parseInt(to.params.id)
    getSpaceTopics()
  }
  next()
})


store.forumActivate(2)
</script>

<template>
  <div v-loading="!space.loading && topics.list.length > 0">
    <div>
    <div class="page">
      <card style="display: flex; flex-direction: column; width: 100%;">
        <div style="display: flex; justify-content: space-between">
          <lite-card style="width: 150px; direction: ltr;">
            <el-avatar :src="store?.avatarUserUrl(space.avatar)" :size="70">头像</el-avatar>
            <div style="font-size: 22px; font-weight: bold">
              {{space.username}}
              <span>
                <el-icon v-if="space.gender === 1" color="hotpink"><Female></Female></el-icon>
                <el-icon v-else color="dodgerblue"><Male></Male></el-icon>
              </span>
            </div>
          </lite-card>
          <lite-card style="display: flex; font-size:16px;">
            <div style="color: mediumpurple">
              <el-icon style="translate: 0 2px"><Star/></el-icon>
              获赞: {{space.like}}
            </div>
            <div style="margin-left: 10px; color: #67C23A" class="interact-item" @click="space.followerShow = true">
              <el-icon style="translate: 0 2px"><Discount/></el-icon>
              关注: {{space.follow}}</div>
            <div style="margin-left: 10px;color: violet" class="interact-item" @click="space.fansShow = true">
              <el-icon style="translate: 0 2px"><Connection/></el-icon>
              粉丝: {{space.fans}}</div>
          </lite-card>
          <lite-card>
            <div style="display: flex; align-items: center">
              <div style="margin-right: 5px"  v-if="store.user.id !== Number.parseInt(route?.params.id)">
                <el-tooltip :content="space.followed ? '取消关注' : '关注'">
                  <el-button :icon="Select" v-if="!space.followed" circle type="primary" plain
                             @click="following(Number.parseInt(route?.params.id), !space.followed)"></el-button>
                  <el-button :icon="CloseBold" v-else circle type="danger" plain @click="following(id, !space.followed)"></el-button>
                </el-tooltip>
              </div>
              <el-button @click="router.push('/index/user-setting')" type="warning" style="width: 100%;" v-if="store.user.id === Number.parseInt(route?.params.id)">编辑资料</el-button>
            </div>
            <lite-card class="interact-item" style="display: flex; margin-top: 10px; justify-content: flex-end"
                       @click="space.collectShow = true" v-if="store.user.id === Number.parseInt(route?.params.id)">
              <el-icon :size="22"><FolderOpened/></el-icon>
              <div>我的收藏</div>
            </lite-card>
          </lite-card>
        </div>
        <card style="margin: 0;">
          <el-tag>
            <el-text tag="b">个人简介</el-text>
          </el-tag>
          <p></p>
          <el-text>{{space.desc}}</el-text>
        </card>
      </card>
      <card style="margin-top: 5px;position: sticky; top: 0; z-index: 10; ">
        <el-row>
          <el-col :span="11">
            <el-button :icon="ArrowLeft" round color="#b88230" plain @click="router.back()">返回</el-button>
          </el-col>
          <el-col :span="13">
            <el-tag style="flex: 1;" size="large" type="primary" effect="light">我的发布</el-tag>
          </el-col>
        </el-row>
      </card>

      <transition name="el-fade-in" mode="out-in">
        <div>
          <div v-infinite-scroll="getSpaceTopics">
            <lite-card v-for="item in topics.list" class="topic-card" style="margin-top: 10px" @click="clickTopic(item.id, item.ban)">
              <div style="display: flex">
                <div style="margin-left: 7px">
                  <div style="font-size: 12px; color: grey">
                    <el-icon style="translate: 0 1px"><Clock/></el-icon>
                    <span>{{new Date(item.time).toLocaleString()}}</span>
                  </div>
                </div>
              </div>
              <div style="margin-top: 5px">
                <topic-tag :type="item.type"></topic-tag>
                <span style="font-weight: bold; margin-left: 8px">{{item.title}}</span>
              </div>
              <div class="topic-content">
                {{item.text}}
              </div>
              <div style="display: grid; grid-template-columns: repeat(3, 1fr); grid-gap: 10px">
                <el-image class="topic-image" v-for="image in item.images" :src="image"  fit="cover"></el-image>
              </div>
              <div style="display: flex; ">
                <div style="display: flex; font-size: 13px; margin-top: 10px; gap: 10px">
                  <div style="opacity: 0.8">
                    <el-icon style="vertical-align: middle; translate: 0 -2px"><CircleCheck/></el-icon> {{item.like}}点赞
                  </div>
                  <div style="opacity: 0.8">
                    <el-icon style="vertical-align: middle; translate: 0 -2px"><Star/></el-icon> {{item.collect}}收藏
                  </div>
                </div>
                <div style="flex: 1;justify-content: flex-end; display: flex">
                  <el-tag v-if="item.ban" type="danger" size="large">封禁</el-tag>
                </div>
              </div>
            </lite-card>
          </div>
        </div>
      </transition>
    </div>
      <topic-collect-list @close="space.collectShow = false" :show="space.collectShow" ></topic-collect-list>
      <follow-list @close="space.followerShow = false" :show="space.followerShow" :type = 0 :uid="route.params.id"></follow-list>
      <follow-list @close="space.fansShow = false" :show="space.fansShow" :type = 1 :uid="route.params.id"></follow-list>
    </div>
  </div>
</template>

<style scoped>
.page{
  display: flex;
  flex-direction: column;
  width: 700px;
  margin: 10px auto 0 auto;
}
.topic-card{
  padding: 10px;

  transition: scale .3s;

  &:hover{
    scale: 1.015;
    cursor: pointer;
  }
  .topic-content {
    font-size: 13px;
    color: grey;
    margin: 5px 0;
    display: -webkit-box;
    -webkit-box-orient:vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .topic-image{
    width: 100%;
    height: 100%;
    max-height: 110px;
    border-radius: 5px;
  }
}
.interact-item{
  &:hover{
    cursor: pointer;
    scale: 1.1;
    font-weight: bold;
  }
}
</style>