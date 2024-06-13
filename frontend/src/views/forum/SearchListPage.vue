<script setup>

import {router} from "@/router/index.js";
import LiteCard from "@/components/LiteCard.vue";
import {reactive, watch} from "vue";
import {CircleCheck, Clock, Star} from "@element-plus/icons-vue";
import TopicTag from "@/components/TopicTag.vue";
import {get} from "@/net/net.js";
import {useStore} from "@/store/index.js";
import {onBeforeRouteUpdate, useRoute} from "vue-router";
import {ElMessageBox} from "element-plus";


const route = useRoute()
const topics = reactive({
  list: [],
  page:1,
  keyword: '',
  type: 0,
  end: false,
  loading :false
})
const store = useStore ()

function getSearchTopics() {
  if (topics.end) return
  get(`/api/forum/search-topic?keyword=${topics.keyword}&type=${topics.type}&page=${topics.page}`,(data) => {
      if (data) {
        data.forEach(item => {
          topics.list.push(item)
        })
        topics.page++
      }
      if(!data || data.length < 10) {
        topics.end = true
      }
      topics.loading = true
  })
}
function clickTopic(id, ban) {
  if(ban) {
    ElMessageBox.alert('该主题已被封禁', '提示')
  } else {
    router.push({ path: '/index/topic-detail/' + id, query: {from: 'search'} })
  }
}

const created = (text, type) => {
  topics.keyword =  text
  topics.type = type
  getSearchTopics()
}

function reset(text, type){
  topics.list = []
  topics.page = 1
  topics.end = false
  topics.loading = false
  created(text, type)
}


watch(
    () => route.query,
    (newQuery) => {
      // 确保newQuery是对象且存在
      if (newQuery && typeof newQuery === 'object') {
        // 由于首次执行时oldQuery不存在，直接使用newQuery的值进行比较
        // 如果是后续的执行，隐含了oldQuery（即上次的newQuery）存在
        if (newQuery.text !== undefined && (topics.keyword !== newQuery.text || topics.type !== parseInt(newQuery.type, 10))) {
          reset(newQuery.text || '', parseInt(newQuery.type || 0, 10));
          console.log(topics.loading);
        }
      }
    },
    { immediate: true, deep: true },
);
</script>

<template>
  <div>
    <transition name="el-fade-in" mode="out-in" style="width: 700px; margin: 0 auto">
        <div v-if="topics.loading">
          <div style="margin-top: 10px; display: flex; flex-direction: column; gap: 10px" v-infinite-scroll="getSearchTopics">
            <!-- 主题列表 -->
            <lite-card  v-for="item in topics.list" @click="clickTopic(item.id, item.ban)" class="topic-card">
              <div style="display: flex">
                <div>
                  <el-avatar :size="30" :src="store.avatarUserUrl(item.avatar)"></el-avatar>
                </div>
                <div style="margin-left: 7px">
                  <div style="font-size: 13px; font-weight: bold">{{item.username}}</div>
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
            </lite-card >
          </div>
        </div>
    </transition>
  </div>
</template>

<style scoped>
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
</style>