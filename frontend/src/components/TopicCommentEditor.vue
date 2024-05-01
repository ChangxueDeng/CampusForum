<script setup>
import {Delta, Quill, QuillEditor} from "@vueup/vue-quill";
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {ref} from "vue";
import {post} from "@/net/net.js";
import {ElMessage} from "element-plus";
const props = defineProps({
  show: Boolean,
  tid: Number,
  quote: Number
})

const content = ref('')
const emit = defineEmits(['close'])

function submitComment() {
  post('api/forum/add-comment', {
    tid: props.tid,
    quote: props.quote,
    content: JSON.stringify(content.value)
  }, ()=> {
    ElMessage.success('发表评论成功')
    emit('close')
  })
}

const init = ()=> content.value = new Delta()
</script>

<template>
  <div>
    <el-drawer :model-value="show" @close="emit('close')" @open="init"
               direction="btt" :size="270" :close-on-click-modal="false"
               title="发表评论">
      <div>
        <div>
          <quill-editor style="height: 120px" v-model:content="content"
                        placeholder="请发表有效评论"/>
        </div>
        <div style="margin-top: 10px; text-align: right">
          <el-button type="success" plain @click="submitComment">发表评论</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style lang="less" scoped>
:deep(.el-drawer) {
  margin: 30px auto;
  width: 800px;
  border-radius: 10px;
}
:deep(.el-drawer__header) {
  margin: 0;
}
:deep(.el-drawer__body) {
  padding: 10px;
}
</style>