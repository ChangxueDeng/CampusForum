<script setup>
import {useRoute} from "vue-router";
import {get, post} from "@/net/net.js"
import {reactive, ref} from "vue";
import {
  ArrowLeft, ChatLineRound,
  ChatSquare,
  CircleCheck, CloseBold,
  Delete,
  EditPen,
  Female, InfoFilled,
  Male,
  Plus,
  Select,
  Star, Warning
} from "@element-plus/icons-vue";
import {QuillDeltaToHtmlConverter} from "quill-delta-to-html";
import Card from "@/components/Card.vue";
import {router} from "@/router/index.js";
import TopicTag from "@/components/TopicTag.vue";
import InteractButton from "@/components/InteractButton.vue";
import {ElMessage} from "element-plus";
import {useStore} from "@/store/index.js";
import TopicEditor from "@/components/TopicEditor.vue";
import TopicCommentEditor from "@/components/TopicCommentEditor.vue";
import LiteCard from "@/components/LiteCard.vue";

const route = useRoute()
const editorShow = ref(false)
const commentEditorShow = ref(false)
const tid = Number.parseInt(route.params.tid)
const store = useStore()
const topicUpdate = ref(false)
const topic = reactive({
  data: null,
  like: false,
  collect: false,
  comments: null,
  page: 1
})
const comment = reactive({
  quote: null,
})

function convert2Html(content){
  const ops = JSON.parse(content).ops
  const converter = new QuillDeltaToHtmlConverter(ops, {inlineStyles:true})
  return converter.convert();
}


function init() {
  get(`api/forum/topic?tid=${tid}`, (data) => {
    if(data.ban) {
      ElMessage.error("帖子已被封禁")
      router.go(-1)
    } else {
      topic.data = data
      topic.like = data.interact.like
      topic.collect = data.interact.collect
      topic.ban = data.ban
      loadComments(1)
    }
  },(message)=> {
    ElMessage.error(message)
    setInterval(()=>router.go(-1),500)
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
function loadComments(page) {
  topic.comments = null;
  topic.page = page;
  get(`api/forum/comments?tid=${tid}&page=${page}`, (data)=> {
    topic.comments = data
  })
}
function submitCommentAfter() {
  loadComments(Math.floor(++topic.data.commentCount / 10) + 1)
}

function deleteComment(cid) {
  get(`api/forum/delete-comment?cid=${cid}`, ()=> {
    ElMessage.success("删除成功")
    loadComments(topic.page)
  })
}
function following(targetId, state, finish) {
  get(`api/forum/follow-user?targetId=${targetId}&state=${state}`,()=> {
    ElMessage.success(state ? '关注成功' : '取消关注成功')
    finish()
    updateCommentFollow(targetId)
  })
}

function updateCommentFollow(targetId) {
  //遍历评论列表
  for (let i = 0; i < topic.comments.length; i++) {
    if (topic.comments[i].user.id === targetId) {
      topic.comments[i].followed = !topic.comments[i].followed
    }
  }
}
function returnForm() {
    if (route.query.from === 'search' || route.query.from === 'space') {
      router.back()
    }else {
      router.push('/index')
    }
}
function deleteTopic() {
  get(`api/forum/delete-topic?id=${tid}`, ()=> {
    ElMessage.success("删除帖子成功")
    router.push('/index')
  }, (message)=> {
    ElMessage.error(message)
  })
}
</script>

<template>
  <div class="topic-page" v-if="topic.data">
    <div class="topic-main" style="position: sticky; top: 0; z-index: 10">
      <card style="display: flex; width: 100%">
        <el-button :icon="ArrowLeft" size="small" plain round @click="returnForm">返回列表</el-button>

        <div style="text-align: center; flex: 1; font-weight: bold">
          <topic-tag style="margin-right: 5px; translate: 0 -2px" :type="topic.data.type"></topic-tag>
          <span>{{topic.data.title}}</span>
        </div>
      </card>
    </div>
    <div class="topic-main">
      <div class="topic-main-left">
        <div v-if="store.user.id !== topic.data.user.id" style="margin-bottom: 5px">
          <el-tooltip :content="topic.data.followed ? '取消关注' : '关注'">
            <el-tag :type="topic.data.followed ? 'danger' : 'primary'"
                    @click="following(topic.data.user.id, !topic.data.followed, ()=> {topic.data.followed = !topic.data.followed})">
              <el-icon v-if="!topic.data.followed" class="interact-item"><Select/></el-icon>
              <el-icon v-else class="interact-item"><CloseBold/></el-icon>
            </el-tag>
          </el-tooltip>
        </div>
        <el-avatar :src="store.avatarUserUrl(topic.data.user.avatar)" :size="60"
                   @click="router.push(`/index/space/${topic.data.user.id}`)"></el-avatar>
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
          <div class="desc">微信号: {{topic.data.user.wx || '已隐藏或未填写'}}</div>
        </div>
        <el-divider style="margin: 10px 0"></el-divider>
        <div class="desc">{{topic.data.user.desc || '已隐藏或未填写'}}</div>
      </div>
      <div class="topic-main-right">
        <card  v-if="topic.data.contentAbstract.length > 10">
          <div style="text-align: center; "><el-icon :size="36"><ChatLineRound/></el-icon></div>
          <el-divider style="margin: 2px;"></el-divider>
          <card><span style="font-size: 20px;font-weight: bold">Ai摘要：</span>{{topic.data.contentAbstract}}</card>
        </card>
        <div class="topic-content" v-html="convert2Html(topic.data.content)"></div>
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
          <el-popconfirm v-if="store.user.id === topic.data.user.id"
                         cancel-button-text="取消"
                         confirm-button-text="确定"
                         :icon="InfoFilled"
                         @confirm="deleteTopic()"
                         icon-color="#626AEF"
                         title="确认删除吗？">
            <template #reference>
              <interact-button name="删除帖子" style="margin-left: 20px;" color="red"
                               v-if="store.user.id === topic.data.user.id">
                <el-icon style="translate: 0 3px"><Warning/></el-icon>
              </interact-button>
            </template>
          </el-popconfirm>
        </div>
      </div>
    </div>
    <transition name="el-fade-in-linear" mode="out-in">
      <div v-if="topic.comments">
        <div class="topic-main" style="margin-top: 10px" v-for="item in topic.comments">
          <div class="topic-main-left">
            <div v-if="store.user.id !== item.user.id" style="margin-bottom: 5px">
              <el-tooltip :content="item.followed ? '取消关注' : '关注'">
                <el-tag :type="item.followed ? 'danger' : 'primary'"
                        @click="following(item.user.id, !item.followed)">
                  <el-icon v-if="!item.followed" class="interact-item"><Select/></el-icon>
                  <el-icon v-else class="interact-item"><CloseBold/></el-icon>
                </el-tag>
              </el-tooltip>
            </div>
            <el-avatar :src="store.avatarUserUrl(item.user.avatar)" :size="60" @click="router.push(`/index/space/${item.user.id}`)"></el-avatar>
            <div>
              <div style="font-size: 18px; font-weight: bold">
                {{item.user.username}}
                <span style="color: hotpink;" v-if="item.user.gender === 1" >
              <el-icon><Female/></el-icon>
            </span>
                <span style="color: dodgerblue;" v-if="item.user.gender === 0">
              <el-icon><Male/></el-icon>
            </span>
              </div>
              <div class="desc">{{item.user.email}}</div>
            </div>
            <el-divider style="margin: 10px 0"></el-divider>
            <div style="text-align: left; margin: 0 5px;">
              <div class="desc">手机号: {{item.user.phone || '已隐藏或未填写' }}</div>
              <div class="desc">QQ号: {{item.user.qq||'已隐藏或未填写'}}</div>
              <div class="desc">微信号: {{item.user.wx || '已隐藏或未填写'}}}</div>
            </div>
            <el-divider style="margin: 10px 0"></el-divider>
            <div class="desc">{{item.user.desc || '已隐藏或未填写'}}</div>
          </div>

          <div class="topic-main-right">
            <div>
              <div style="font-size: 14px; color: grey; text-align: left">评论时间: {{new Date(item.time).toLocaleString()}}</div>
            </div>
            <div v-if="item.quote" class="comment-quote">
              回复: {{item.quote}}
            </div>
            <div class="topic-content" v-if="!item.ban" v-html="convert2Html(item.content)"></div>
            <el-alert v-if="item.ban" style="margin-top: 10px;" type="error" :closable = "false">此评论已被封禁</el-alert>
            <div style="text-align: right">
              <el-link :icon="ChatSquare" @click="commentEditorShow = true; comment.quote = item"
                       type="info" v-if="!item.ban">&nbsp;回复评论</el-link>
              <el-link :icon="Delete" type="danger" v-if="item.user.id === store.user.id"
                       style="margin-left: 20px" @click="deleteComment(item.id)">&nbsp;删除评论</el-link>
            </div>
          </div>
        </div>
        <div style="width: fit-content; margin: 20px auto">
          <el-pagination
              background
              layout="prev, pager, next"
              :page-size="10"
              :total="topic.data.commentCount"
              :current-page="topic.page"
              @current-change="loadComments"
              hide-on-single-page>
          </el-pagination>
        </div>
      </div>
    </transition>
    <topic-editor :show="editorShow" @close="editorShow = false" v-if="topic.data"
                  @success="editorShow = false"
                  :default-text="topic.data.content" :default-title="topic.data.title"
                  :default-type="topic.data.type" submit-button="立即修改" :submit="update"/>
    <div class="add-comment" @click="commentEditorShow = true">
      <el-icon><Plus/></el-icon>
    </div>
    <topic-comment-editor :show="commentEditorShow" @close="commentEditorShow = false; comment.quote = null"
                          :tid="tid"  @success="submitCommentAfter" :quote="comment.quote"></topic-comment-editor>
  </div>
</template>

<style scoped>
.interact-item{
  &:hover{
    cursor: pointer;
    scale: 1.1;
    font-weight: bold;
  }
}
.comment-quote{
  font-size: 13px;
  color: grey;
  background-color: rgba(94, 94, 94, 0.2);
  padding: 10px;
  margin-top: 10px;
  border-radius: 5px;
}
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
    width: 600px;
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