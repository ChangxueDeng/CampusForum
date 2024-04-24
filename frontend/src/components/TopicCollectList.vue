<script setup>
import {get} from "@/net/net.js";
import {ref} from "vue";
import LiteCard from "@/components/LiteCard.vue";
import {router} from "@/router/index.js";
import TopicTag from "@/components/TopicTag.vue";
import {ElMessage} from "element-plus";

defineProps({
  show: Boolean,
})

const list = ref([])
const emit = defineEmits(['close'])

function init() {
  get('api/forum/collects', data=>{
      list.value = data
  })
}

function deleteCollect(index, tid) {
  get(`api/forum/interact?tid=${tid}&type=collect&state=false`, ()=>{
    ElMessage.success("取消收藏成功!")
    list.value.splice(index, 1)
  })
}
</script>

<template>
  <el-drawer :model-value="show" @close="emit('close')" @open="init" title="我的收藏">
    <div class="collect-list">
      <lite-card v-for="(item, index) in list" class="topic-card" @click="router.push(`/index/topic-detail/${item.id}`)">
        <topic-tag :type="item.type"></topic-tag>
        <div class="topic-title">
          <b>{{item.title}}</b>
        </div>
        <el-link type="danger" @click.stop="deleteCollect(index, item.id)">删除</el-link>
      </lite-card>
    </div>
  </el-drawer>
</template>

<style scoped>
.collect-list{
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.topic-card{
  background-color: rgba(128, 128, 128, 0.2);
  transition: .3s;
  display: flex;
  justify-content: space-between;
  .topic-title{
    margin-left: 4px;
    font-size: 14px;
    flex: 1;
    white-space: nowrap;
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;

  }
  &:hover{
    scale: 1.02;
    cursor: pointer;
  }
}
</style>