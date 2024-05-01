<script setup>
import {useRoute} from "vue-router";
import {get, post} from "@/net/net.js"
import {reactive, ref} from "vue";
import axios from "axios";
import {ArrowLeft, CircleCheck, EditPen, Female, Male, Plus, Star} from "@element-plus/icons-vue";
import {computed} from "vue";
import {QuillDeltaToHtmlConverter} from "quill-delta-to-html";
import Card from "@/components/Card.vue";
import {router} from "@/router/index.js";
import TopicTag from "@/components/TopicTag.vue";
import InteractButton from "@/components/InteractButton.vue";
import {ElMessage} from "element-plus";
import {useStore} from "@/store/index.js";
import TopicEditor from "@/components/TopicEditor.vue";
import TopicCommentEditor from "@/components/TopicCommentEditor.vue";

const route = useRoute()
const editorShow = ref(false)
const commentEditorShow = ref(false)
const tid = route.params.tid
const store = useStore()
const topicUpdate = ref(false)
const topic = reactive({
  data: null,
  like: false,
  collect: false,
  comments: []
})
const comment = reactive({
  quote: -1,
})
const content = computed(()=>{
  const ops = JSON.parse(topic.data.content).ops
  const converter = new QuillDeltaToHtmlConverter(ops, {inlineStyles:true})
  return converter.convert();

})


function init() {
  get(`api/forum/topic?tid=${tid}`, (data) => {
    topic.data = data
    topic.like = data.interact.like
    topic.collect = data.interact.collect
  })
}
init()

function update(editor, emit) {
  post('api/forum/update-topic', {
    id: tid,
    type: editor.type.id,
    title: editor.title,
    content: editor.text,
  }, () => {
    ElMessage.success("修改帖子成功")
    emit()
    init()
  }, (message)=> {
    ElMessage.error(message)
    emit()
  })
}
function interact(type, message) {
  get(`api/forum/interact?tid=${tid}&type=${type}&state=${!topic[type]}`, ()=> {
    topic[type] = !topic[type]
    if (topic[type]) {
      ElMessage.success(`${message}成功!`)
    } else {
      ElMessage.warning(`已取消${message}!`)
    }
  })
}

</script>

<template>
  <div class="topic-page" v-if="topic.data">
    <div class="topic-main" style="position: sticky; top: 0; z-index: 10">
      <card style="display: flex; width: 100%">
        <el-button :icon="ArrowLeft" size="small" plain round @click="router.push('/index')">返回列表</el-button>

        <div style="text-align: center; flex: 1; font-weight: bold">
          <topic-tag style="margin-right: 5px; translate: 0 -2px" :type="topic.data.type"></topic-tag>
          <span>{{topic.data.title}}</span>
        </div>
      </card>
    </div>
    <div class="topic-main">
      <div class="topic-main-left">
        <el-avatar :src="axios.defaults.baseURL + '/images' + topic.data.user.avatar" :size="60"></el-avatar>
        <div>
          <div style="font-size: 18px; font-weight: bold">
            {{topic.data.user.username}}
            <span style="color: hotpink;" v-if="topic.data.user.gender === 1" >
              <el-icon><Female/></el-icon>
            </span>
            <span style="color: dodgerblue;" v-if="topic.data.user.gender === 0">
              <el-icon><Male/></el-icon>
            </span>
          </div>
          <div class="desc">{{topic.data.user.email}}</div>
        </div>
        <el-divider style="margin: 10px 0"></el-divider>
        <div style="text-align: left; margin: 0 5px;">
          <div class="desc">手机号: {{topic.data.user.phone || '已隐藏或未填写' }}</div>
          <div class="desc">QQ号: {{topic.data.user.qq||'已隐藏或未填写'}}</div>
          <div class="desc">微信号: {{topic.data.user.wx || '已隐藏或未填写'}}}</div>
        </div>
        <el-divider style="margin: 10px 0"></el-divider>
        <div class="desc">{{topic.data.user.desc || '已隐藏或未填写'}}</div>
      </div>

      <div class="topic-main-right">
        <div class="topic-content" v-html="content"></div>
        <el-divider></el-divider>
        <div>
          <div style="font-size: 14px; color: grey; text-align: center">发帖时间: {{new Date(topic.data.time).toLocaleString()}}</div>
        </div>
        <div style="text-align: right; margin-top: 30px">
          <interact-button name="编辑帖子" style="margin-left: 20px;" color="dodgerblue"
                           @click="editorShow = true" v-if="store.user.id === topic.data.user.id">
            <el-icon style="translate: 0 3px"><EditPen/></el-icon>
          </interact-button>
          <interact-button name="点个赞" style="margin-left: 20px" color="pink" check-name="已点赞" :check="topic.like"
                           @click="interact('like', '点赞')">
            <el-icon style="translate: 0 3px"><CircleCheck/></el-icon>
          </interact-button>
          <interact-button name="收藏" style="margin-left: 20px" color="orange" check-name="已收藏" :check="topic.collect"
                            @click="interact('collect', '收藏')">
            <el-icon style="translate: 0 3px"><Star/></el-icon>
          </interact-button>
        </div>
      </div>
    </div>
    <topic-editor :show="editorShow" @close="editorShow = false" v-if="topic.data"
                  @success="editorShow = false"
                  :default-text="topic.data.content" :default-title="topic.data.title"
                  :default-type="topic.data.type" submit-button="立即修改" :submit="update"/>
    <div class="add-comment" @click="commentEditorShow = true">
      <el-icon><Plus/></el-icon>
    </div>
    <topic-comment-editor :show="commentEditorShow" @close="commentEditorShow = false"
                          :tid="tid" :quote="comment.quote"></topic-comment-editor>
  </div>
</template>

<style scoped>
.add-comment{
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--el-bg-color-overlay);
  box-shadow: var(--el-box-shadow-lighter);
  font-size: 18px;
  color: var(--el-color-primary);
  text-align: center;
  line-height: 45px;
  &:hover{
    cursor: pointer;
    background-color: var(--el-color-primary);
    color: white;
  }
}
.topic-page{
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px 0;
}
.topic-main{
  display: flex;
  border-radius: 8px;
  margin: 0 auto;
  background-color: var(--el-bg-color);
  width: 800px;

  .topic-main-left{
    width: 200px;
    padding: 10px;
    text-align: center;
    border-right: solid 1px var(--el-border-color);
    .desc{
      font-size: 12px;
      color: grey;
    }
  }
  .topic-main-right{
    width: 600pc;
    padding: 10px 20px;
    display: flex;
    flex-direction: column;
    .topic-content{
      font-size: 14px;
      line-height: 22px;
      opacity: 0.8;
      flex: 1;
    }
  }
}
</style>