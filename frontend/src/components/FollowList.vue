<script setup>
import {get} from "@/net/net.js";
import {ref} from "vue";
import LiteCard from "@/components/LiteCard.vue";
import {router} from "@/router/index.js";
import TopicTag from "@/components/TopicTag.vue";
import {ElMessage} from "element-plus";
import {useStore} from "@/store/index.js";
import {Female, Male} from "@element-plus/icons-vue";

const props = defineProps({
  show: Boolean,
  type: Number,
  uid: String,
})
const loading = ref(false)
const store = useStore()
const list = ref([])
const emit = defineEmits(['close'])
function getFollowList() {
  console.log(props.uid)
  get(`api/forum/space-follows?type=${props.type}&id=${props.uid}`, (data)=> {
    list.value = data
    loading.value = true
  })
}
function init() {
  getFollowList()
}
</script>

<template>
    <el-drawer :model-value="show" @close="emit('close')" @open="init" title="我的收藏" size="200px" direction="ltr">
      <div class="collect-list" v-loading="!loading">
        <lite-card v-for="item in list" class="topic-card">
          <div class="topic-title" @click="router.push(`/index/space/${item.id}`);emit('close')">
            <el-avatar :src="store.avatarUserUrl(item.avatar)"></el-avatar>
            <el-icon v-if="item.gender === 1" color="hotpink"><Female></Female></el-icon>
            <el-icon v-else color="dodgerblue"><Male></Male></el-icon>
            <el-text size="large">{{item.username}}</el-text>
          </div>
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