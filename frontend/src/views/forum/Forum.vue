<script setup>

import {get} from "@/net/net.js";
import {useStore} from "@/store/index.js";


const store = useStore()

get('api/forum/types', data => {
  const array = []
  array.push({name: '全部', id: 0, color: 'linear-gradient(45deg, white, red, orange, gold, green, blue)'})
  data.forEach(d => array.push(d))
  store.forum.types = array
})

</script>

<template>
  <div>
    <router-view v-slot="{Component}">
      <transition name="el-fade-in=linear" mode="out-in">
        <keep-alive include="TopicList">
          <component :is="Component"></component>
        </keep-alive>
      </transition>
    </router-view>
    <el-backtop target=".main-content-page .el-scrollbar__wrap" :right="20" :bottom="70"></el-backtop>
  </div>

</template>

<style scoped>

</style>