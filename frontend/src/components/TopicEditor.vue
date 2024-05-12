<script setup>
import {Check, Document} from "@element-plus/icons-vue";
import {computed, reactive} from "vue";
import {Delta, Quill, QuillEditor} from "@vueup/vue-quill";
import ImageResize from "quill-image-resize-vue"
import {ImageExtend, QuillWatch} from "quill-image-super-solution-module"
import axios from "axios";
import {accessHeader, post} from "@/net/net.js";
import {ElMessage} from "element-plus";
import {ref} from "vue";
import ColorDot from "@/components/ColorDot.vue";
import {useStore} from "@/store/index.js";
const props = defineProps({
  show: Boolean,
  defaultTitle : {
    default: '',
    type: String
  },
  defaultText: {
    default: '',
    type: String
  },
  defaultType: {
    default: 1,
    type: Number
  },
  submitButton: {
    default: '立即发表主题',
    type: String
  },
  submit: {
    default: (editor, emit) => {
      post('api/forum/create-topic', {
        type: editor.type.id,
        title: editor.title,
        content: editor.text,
      }, () => {
        ElMessage.success("发布帖子成功!")
        emit()
      }, (message)=>{
        ElMessage.error(message)
        emit()
      })
    },
    type: Function
  }
})
const store = useStore()
Quill.register('modules/imageResize', ImageResize)
Quill.register('modules/ImageExtend', ImageExtend)
//
// function create(editor, emit) {
//   post('api/forum/create-topic', {
//     type: editor.type.id,
//     title: editor.title,
//     content: editor.text,
//   }, ()=> {
//     ElMessage.success("发帖成功!")
//     emit()
//   }, (message) => {
//     ElMessage.error(message)
//     emit()
//   })
// }
//
// function update(editor, emit) {
//   post('api/forum/update-topic', {
//     type: editor.type.id,
//     id: props.submitType,
//     title: editor.title,
//     content: editor.text,
//   }, ()=> {
//     ElMessage.success("修改成功!")
//     emit()
//   }, (message) => {
//     ElMessage.error(message)
//     emit()
//   })
// }

// const types = [
//   {id: 1, name: '日常闲聊', desc: '在这里分享你的各种日常'},
//   {id: 2, name: '真诚交友', desc: '在论坛里寻找与自己志同道合的朋友'},
//   {id: 3, name: '问题反馈', desc: '反馈你在校园里遇到的各种问题'},
//   {id: 4, name: '恋爱官宣', desc: '向大家展示你的恋爱成果'},
//   {id: 5, name: '踩坑记录', desc: '将你遇到的坑分享给大家，防止其他人再次入坑'}
// ]
const editor = reactive({
  type: null,
  title: '',
  text: '',
  loading: false,
})
const editorOptions = {
  modules: {
    toolbar: {
      container: [
          "bold", "italic", "underline", "strike", "clean",
          {color : []}, {'background' : []},
        {size: ["small", false, "large", "huge"]},
        {header: [1, 2, 3, 4, 5, 6, false]},
        {list: "ordered"}, {list: "bullet"},{align :[]},
          "blockquote", "code-block", "link", "image",
        {indent: '-1'}, {indent: '+1'}
      ],
      handlers: {
        'image' : function () {
          QuillWatch.emit(this.quill.id)
        }
      },
    },
    imageResize: {
      modules: ['Resize', 'DisplaySize']
    },
    ImageExtend: {
      action: axios.defaults.baseURL + "/api/image/cache",
      name: 'file',
      size: 5,
      loading: true,
      accept: '/image/png, image/jpeg',
      response: (resp) => {
        if (resp.data) {
          console.log(resp.data)
          return axios.defaults.baseURL + "/images" + resp.data
        } else {
          return null;
        }
      },
      methods: 'POST',
      headers: xhr => {
        xhr.setRequestHeader('Authorization', accessHeader().Authorization)
      },
      start: () => {
        editor.uploading = true;
      },
      success: () => {
        ElMessage.success('图片上传成功')
        editor.uploading = false
      },
      error: ()=> {
        ElMessage.warning("图片上传失败，请联系管理员")
        editor.uploading = false
      }
    }
  }
}

const emit = defineEmits(['close', 'success'])

function deltaToText(delta) {
  if (!delta.ops) return ""
  let str = ""
  for (let op of delta.ops) {
    let image = op.insert.image
    if (image === undefined) {
      str += op.insert
    }
  }
  return str.replace(/\s/g, "")
}

function submitTopic() {
  if (contentLength > 20000) {
    ElMessage.warning("主题帖内容字数过多!")

  } else if (!editor.title) {
    ElMessage.warning("请输入标题!")
  } else if (!editor.type) {
    ElMessage.warning("请选择帖子内容!")
  } else {
    props.submit(editor, () => emit('success'))
  }
}


const editorRef = ref()

function initEditor() {
  if (props.defaultText) {
    editor.text = new Delta(JSON.parse(props.defaultText))
  } else {
    editorRef.value.setContents('', 'user')
  }
  editor.type = store.findTyById(props.defaultType)
  editor.title = props.defaultTitle
}

const contentLength = computed(()=> deltaToText(editor.text).length)

</script>

<template>
  <div>
    <el-drawer :model-value="show" direction="btt"
               :size="650"
               :close-on-click-modal="false"
               @close="emit('close')"
               @open="initEditor()">
      <template #header>
        <div>
          <div style="font-weight: bold;"> 发表新的帖子</div>
          <div style="font-size: 13px">发布帖子请遵守论坛规则</div>
        </div>
      </template>
      <div style="display: flex; gap: 10px;">
        <div style="width: 150px">
          <el-select placeholder="请选择主题类型" value-key="id" v-model="editor.type" :disabled="!store.forum.types.length">
            <el-option v-for="item in store.forum.types.filter(type => type.id > 0)" :value="item" :label="item.name">
              <div>
                <color-dot :color="item.color ? item.color : '#FFFFFF' "></color-dot>
                <span style="margin-left: 10px">{{item.name}}</span>
              </div>
            </el-option>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input placeholder="请输入帖子标题" :prefix-icon="Document" v-model="editor.title" min="1" maxlength="30">
          </el-input>
        </div>
      </div>
      <div style="margin-top: 5px; font-size: 13px; color: grey">
        {{editor.type ? editor.type.desc : '请选择帖子主题'}}
      </div>
      <div style="margin-top: 5px; height: 440px; overflow: hidden" v-loading="editor.uploading" element-loading-text="正在上传图片，请稍后...">
        <quill-editor v-model:content="editor.text"
                      style="height: calc(100% - 45px);"
                      content-type="delta"
                      placeholder="分享你的想法吧...."
                      :options="editorOptions"
                      ref="editorRef">
        </quill-editor>
      </div>
      <div style="display: flex; justify-content: space-between; margin-top: 10px">
        <div style="color: grey; font-size: 13px">当前字数: {{contentLength}} (最大支持20000字)</div>
        <div>
          <el-button :icon="Check" type="success" @click="submitTopic()">{{props.submitButton}}</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style lang="less" scoped>
:deep(.el-drawer) {
  margin: auto;
  width: 800px;
  border-radius: 10px 10px 0 0;
}
:deep(.el-drawer__header) {
  margin: 0;
}
:deep(.el-drawer).btt{
    width: 800px;
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