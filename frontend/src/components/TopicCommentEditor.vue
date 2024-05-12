<script setup>
import {Delta, Quill, QuillEditor} from "@vueup/vue-quill";
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {computed, ref} from "vue";
import {post} from "@/net/net.js";
import {ElMessage} from "element-plus";
const props = defineProps({
  show: Boolean,
  tid: Number,
  quote: Object
})

const content = ref('')
const emit = defineEmits(['close','success'])

function submitComment() {
  if (deltaToText(content.value).length > 200) {
    ElMessage.warning("评论内容不能超过200字")
  }else {
    post('api/forum/add-comment', {
      tid: props.tid,
      quote: props.quote ? props.quote.id : -1,
      content: JSON.stringify(content.value)
    }, ()=> {
      ElMessage.success('发表评论成功')
      emit('close')
      emit('success')
    })
  }
}

function delta2SimpleText(delta) {
  let str = ''
  for (let op of JSON.parse(delta).ops) {
    str += op.insert
  }
  if (str.length < 35) str = str.substring(0, 35) + "..."
  return str
}
const init = ()=> content.value = new Delta()

function deltaToText(delta) {
  if (!delta?.ops) return ""
  let str = ""
  for (let op of delta.ops) {
    let image = op.insert.image
    if (image === undefined) {
      str += op.insert
    }
  }
  return str.replace(/\s/g, "")
}
</script>

<template>
  <div>
    <el-drawer :model-value="show" @close="emit('close')" @open="init"
               direction="btt" :size="270" :close-on-click-modal="false"
               :title="quote ? `发表对评论: ${delta2SimpleText(quote.content)} 的回复` : '发表帖子回复'">
      <div>
        <div>
          <quill-editor style="height: 120px" v-model:content="content"
                        placeholder="请发表有效评论"/>
        </div>
        <div style="margin-top: 10px; display: flex">
          <div style="flex: 1; font-size: 13px; color: grey">当前字数: {{deltaToText(content).length}} 字（最大字数200）</div>
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
.ql-toolbar {
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color) !important;
}
.ql-container {
  border-radius: 0 0 5px 5px;
  border-color: var(--el-border-color) !important;
}
.ql-editor.ql-blank::before {
  color: var(--el-text-color-placeholder) !important;
  font-style: normal !important;
}
.ql-editor{
  font-size: 15px;
}
</style>