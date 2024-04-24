<script setup>

import LiteCard from "@/components/LiteCard.vue";
import {
  ArrowRight, ArrowRightBold,
  Calendar, CircleCheck,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen, FolderOpened,
  Link,
  Microphone,
  Picture, Star
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref, watch} from "vue";
import {get} from "@/net/net.js";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {useStore} from "@/store/index.js";
import axios from "axios";
import ColorDot from "@/components/ColorDot.vue";
import {router} from "@/router/index.js";
import TopicTag from "@/components/TopicTag.vue";
import TopicCollectList from "@/components/TopicCollectList.vue";
const today = computed(()=> {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth() + 1 } 月 ${date.getDate()} 日`
})

const store = useStore()

const weather = reactive({
  location : {},
  now: {},
  hourly: [],
  success: false
})
const collectShow = ref(false);
const editorShow = ref(false);
const topics = reactive({
  list: [],
  type: 0,
  page: 0,
  end: false, //是否到达最后
  top: []
})
watch(() => topics.type ,()=>{
  resetList()
}, {immediate: true})

function getTopicList() {
  console.log(topics.page)
  if(topics.end) return
  get(`api/forum/list-topic?page=${topics.page}&type=${topics.type}`, (data) => {
    if (data) {
      data.forEach (t => topics.list.push(t))
      topics.page++
    }
    if(!data || data.length < 10) {
      topics.end = true
    }
  })
}

function resetList() {
  topics.page = 0
  topics.list = []
  topics.end = false
  getTopicList()
}


get('api/forum/top-topic', data => topics.top = data)
navigator.geolocation.getCurrentPosition(position => {
  const longitude = position.coords.longitude;
  const latitude = position.coords.latitude;
  get(`api/forum/weather?longitude=${longitude}&latitude=${latitude}`, (data)=>{
      Object.assign(weather, data)
      weather.success = true;
  })
}, error => {
  console.log(error)
  ElMessage.warning("位置信息获取超时，请检查网络设置")
}, {
  timeout: 5000,
  enableHighAccuracy: true
})
</script>

<template>
  <div style="display: flex; margin: 20px auto; gap: 20px; max-width: 950px">
    <div style="flex: 1;">
      <lite-card style="padding: 0" @click="editorShow = true">
        <div class="creat-topic">
          <el-icon style="translate: 0 2px"><EditPen/></el-icon>
          点击发表主题...
        </div>
        <div style="margin-top: 10px; padding-bottom: 5px; margin-left: 5px; display: flex; gap: 13px; font-size: 18px; color: grey;">
          <el-icon><Edit/></el-icon>
          <el-icon><Document/></el-icon>
          <el-icon><Compass/></el-icon>
          <el-icon><Picture/></el-icon>
          <el-icon><Microphone/></el-icon>
        </div>
      </lite-card>
      <lite-card style="margin-top: 10px; display: flex; flex-direction: column; gap: 10px">
        <div v-for="item in topics.top" class="top-topic" @click="router.push('/index/topic-detail/' + item.id)">
          <el-tag type="info" size="small">置顶</el-tag>
          <div>{{item.title}}</div>
          <div>{{new Date(item.time).toLocaleString()}}</div>
        </div>
      </lite-card>
      <lite-card style="margin-top: 10px; display: flex; gap: 8px">
        <div :class="`type-select-card${topics.type === item.id ? 'active' : ''}`" v-for="item in store.forum.types"
            @click="topics.type = item.id">
          <ColorDot :color="item.color"></ColorDot>
          <span style="margin-left: 5px">{{item.name}}</span>
        </div>
      </lite-card>

      <transition name="el-fade-in" mode="out-in">
        <div v-if="topics.list?.length">
          <div style="margin-top: 10px; display: flex; flex-direction: column; gap: 10px" v-infinite-scroll="getTopicList">
            <!-- 主题列表 -->
            <lite-card  v-for="item in topics.list" @click="router.push('/index/topic-detail/' + item.id)" class="topic-card">
              <div style="display: flex">
                <div>
                  <el-avatar :size="30" :src="`${axios.defaults.baseURL}/images${item.avatar}`"></el-avatar>
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
              <div style="display: flex; gap: 10px; font-size: 13px; margin-top: 10px">
                <div style="opacity: 0.8">
                  <el-icon style="vertical-align: middle; translate: 0 -2px"><CircleCheck/></el-icon> {{item.like}}点赞
                </div>
                <div style="opacity: 0.8">
                  <el-icon style="vertical-align: middle; translate: 0 -2px"><Star/></el-icon> {{item.collect}}收藏
                </div>
              </div>
            </lite-card >
          </div>
        </div>
      </transition>
    </div>
    <div style="width: 300px;">
      <div style="position: sticky;top: 20px">
        <lite-card style="margin-bottom: 10px">
          <div class="collect-list-button" @click="collectShow = true">
            <span><el-icon style="translate: 0 2px"><FolderOpened/></el-icon>查看我的收藏</span>
            <el-icon style="translate: 0 2px"><ArrowRightBold/></el-icon>
          </div>
        </lite-card>
        <lite-card>
          <div style="font-weight: bold">
            <el-icon style="translate: 0 2px"> <CollectionTag/></el-icon>
            论坛公告
          </div>
          <el-divider style="margin: 10px 0"> </el-divider>
          <div style="font-size: 14px; margin: 10px; color: grey">
            神经语言模型使用分布式表示（也称为嵌入）来表示词汇。这些嵌入是低维空间的连续向量，
            能够捕获词汇的语义和语法特征。通过训练，相似的词汇在嵌入空间中会有接近的位置。
          </div>
        </lite-card>
        <lite-card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon style="translate: 0 2px"><Calendar/></el-icon>
            天气信息
          </div>
          <el-divider style="margin: 10px 0"></el-divider>
          <div>
            <weather :data="weather"></weather>
          </div>
        </lite-card>
        <lite-card style="margin-top: 10px">
          <div class="info-text">
            <div>当前日期</div>
            <div>{{today}}</div>

          </div>
          <div class="info-text">
            <div>当前IP地址</div>
            <div>127.0.0.1</div>
          </div>
        </lite-card>
        <div style="font-size: 14px; margin-top: 14px; color: grey">
          <el-icon><Link/></el-icon>
          友情链接
          <el-divider style="margin: 10px 0"></el-divider>
        </div>
        <div style="display: grid; grid-template-columns: repeat(2, 1fr); grid-gap: 10px; margin-top: 10px">
          <div class="friend-link">
            <el-image src="https://element-plus.org/images/js-design-banner.jpg"></el-image>
          </div>
          <div class="friend-link">
            <el-image src="https://element-plus.org/images/sponsors/vform-banner.png"></el-image>
          </div>
          <div class="friend-link">
            <el-image src="https://element-plus.org/images/sponsors/jnpfsoft.jpg"/>
          </div>
        </div>
      </div>

    </div>
    <topic-editor :show="editorShow" @close="editorShow = false" @success="editorShow = false;resetList()">
    </topic-editor>
    <topic-collect-list :show="collectShow" @close="collectShow = false"></topic-collect-list>
  </div>
</template>

<style lang="less" scoped>
.friend-link{
  border-radius: 5px;
  overflow: hidden;
}
.creat-topic{
  background-color: #efefef;
  color: grey;
  height: 40px;
  border-radius: 5px;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;
  &:hover{
    cursor: pointer;
  }
}
.info-text{
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px
}
.dark  {
  .creat-topic{
    background-color: #2b2e34;
  }
  .type-select-card{
    background-color: #282828;
    &.active{
      border: solid 1px #64564b;
    }
    &:hover{
      background-color: #5e5e5e;
    }
  }

}
.top-topic{
  display: flex;
  div:first-of-type {
    font-size: 14px;
    margin-left: 10px;
    font-weight: bold;
    opacity: 0.7;
    transition: color .3s;
    &:hover {
      cursor: pointer;
      color: grey;
    }
  }
  div:nth-of-type(2) {
    flex: 1;
    color: grey;
    font-size: 13px;
    text-align: right;
  }
  &:hover{
    color: grey;
  }
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
.type-select-card{
  background-color: #f5f5f5;
  padding: 2px 7px;
  font-size: 14px;
  border-radius: 3px;
  box-sizing: border-box;
  transition: background-color .3s;
  &.active{
    border: solid 1px #7fd697;
  }
  &:hover{
    cursor: pointer;
    background-color: #dadada;
  }
}
.collect-list-button{
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: .3s;
  &:hover{
    cursor: pointer;
    opacity: 0.8;
  }
}
</style>