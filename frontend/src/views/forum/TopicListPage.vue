<script setup>

import LiteCard from "@/components/LiteCard.vue";
import {Calendar, CollectionTag, EditPen, Link} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref} from "vue";
import {get} from "@/net/net.js";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";

const today = computed(()=> {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth()} 月 ${date.getDay()} 日`
})

const weather = reactive({
  location : {},
  now: {},
  hourly: [],
  success: false
})

const editorShow = ref(false);

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
  <div style="display: flex; margin: 20px auto; gap: 20px; max-width: 900px">
    <div style="flex: 1;">
      <lite-card style="padding: 0" @click="editorShow = true">
        <div class="creat-topic">
          <el-icon style="translate: 0 2px"><EditPen/></el-icon>
          点击发表主题...
        </div>
      </lite-card>
      <lite-card style="margin-top: 10px; height: 30px">

      </lite-card>
      <div style="margin-top: 10px; display: flex; flex-direction: column; gap: 10px">
        <lite-card style="height: 150px" v-for="item in 10">

        </lite-card>
      </div>
    </div>
    <div style="width: 300px;">
      <div style="position: sticky;top: 20px">
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
    <topic-editor :show="editorShow" @close="editorShow = false">
    </topic-editor>
  </div>
</template>

<style scoped>
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
</style>