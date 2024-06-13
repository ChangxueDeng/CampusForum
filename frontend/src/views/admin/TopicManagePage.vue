<script setup>
import {reactive, ref} from "vue";
import {get} from "@/net/net.js";
import {useStore} from "@/store/index.js";
import Card from "@/components/Card.vue";
import TopicEditor from "@/components/TopicEditor.vue";
import {InfoFilled} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
const topic = reactive({
  page: 1,
  data: null,
  loading: false,
  banned :false,
  topped: false,
})

const store = useStore()

function getTopics(page) {
  topic.page = page
  get(`api/admin/topics?page=${page}`, (data)=> {
    topic.data = data;
    topic.loading = true;
  })
}
function deleteTopic(id) {
  get(`api/admin/delete-topic?id=${id}`, ()=> {
    ElMessage.success('删除成功')
    getTopics(topic.page)
  })
}
function banTopic(id, ban, finish) {
  topic.banned = ban
  get(`api/admin/ban-topic?id=${id}&ban=${ban}`, ()=> {
    ElMessage.success(ban ? '解封成功' : '封禁成功')
    topic.banned = !ban
    finish()
  })
}
function topTopic(id, top, finish) {
  topic.topped = top
  get(`api/admin/top-topic?id=${id}&top=${top}`, ()=> {
    ElMessage.success(top ? '取消置顶成功' : '置顶成功')
    topic.topped = !top
    finish()
  })
}
function openTopic(row) {
  window.open(`/index/topic-detail/${row.id}`)
}
store.adminActivate('topic')
getTopics(1)
</script>

<template>
  <div v-loading="!topic.loading">
    <div style="display: flex; justify-content: space-between">
      <card style="text-align: center; width: 100px; font-size: 18px; font-weight: bold;display:flex;justify-content: center; align-items: center">
        <div>帖子管理</div>
      </card>
      <card style="text-align: center; width: 300px; margin-left: 30px; display: flex;justify-content: center">
        <div style="margin-top: 5px">当前主题贴数量:{{store.admin.topicCount}}</div>
      </card>
    </div>
    <div style="margin-top: 10px">
      <card>
        <div style="overflow: hidden;">
          <div style="height: 620px;">
            <el-table :data="topic.data" width="100%" fit border table-layout="auto" height="100%" size="large">
              <template #empty>
                <el-empty description="暂无数据"></el-empty>
              </template>
              <el-table-column label="Id" prop="id"/>
              <el-table-column label="标题" prop="title"/>
              <el-table-column label="内容" prop="content" show-overflow-tooltip/>
              <el-table-column label="摘要" prop="contentAbstract" show-overflow-tooltip/>
              <el-table-column label="发布者" prop="uid"/>
              <el-table-column label="类型" prop="type"/>
              <el-table-column label="发布时间" prop="time"/>
              <el-table-column label="封禁" prop="ban" align="center">
                <template #default="scoop">
                <el-popconfirm cancel-button-text="取消"
                               confirm-button-text="确定"
                               :icon="InfoFilled"
                               @confirm="banTopic(scoop.row.id, scoop.row.ban, ()=> {
                                 scoop.row.ban = topic.banned
                               })"
                               icon-color="#626AEF"
                               :title="scoop.row.ban ? '确认解封吗？' : '确认封禁吗？'">
                  <template #reference>
                    <div class="ban">
                      <el-tag v-if="!scoop.row.ban" type="success">否</el-tag>
                      <el-tag v-else-if="scoop.row.ban" type="danger">是</el-tag>
                    </div>
                  </template>
                </el-popconfirm>
                </template>
              </el-table-column>
              <el-table-column label="置顶" prop="top" align="center">
                <template #default="scoop">
                  <el-popconfirm cancel-button-text="取消"
                                 confirm-button-text="确定"
                                 :icon="InfoFilled"
                                 @confirm="topTopic(scoop.row.id, scoop.row.top, ()=> {
                                 scoop.row.top = topic.topped
                               })"
                                 icon-color="#626AEF"
                                 :title="scoop.row.top ? '确认取消置顶吗？' : '确认置顶吗？'">
                    <template #reference>
                      <div class="ban">
                        <el-tag v-if="!scoop.row.top" type="success">否</el-tag>
                        <el-tag v-else-if="scoop.row.top" type="danger">是</el-tag>
                      </div>
                    </template>
                  </el-popconfirm>
                </template>
              </el-table-column>
              <el-table-column align="center">
                <template #default="scoop">
                    <el-button type="success" @click="openTopic(scoop.row)">跳转</el-button>
                  <el-popconfirm cancel-button-text="取消"
                                 confirm-button-text="确定"
                                 :icon="InfoFilled"
                                 @confirm="deleteTopic(scoop.row.id)"
                                 icon-color="#626AEF"
                                 title="确定删除吗?" >
                    <template #reference>
                      <el-button type="danger">删除</el-button>
                    </template>
                  </el-popconfirm>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </card>
      <card style="margin-top: 10px; height: 50px">
        <div style="width: fit-content; margin: 0 auto">
          <el-pagination
              background
              layout="prev, pager, next"
              :page-size="10"
              :total="store.admin.topicCount"
              :current-page="topic.page"
              @current-change="getTopics"
              hide-on-single-page>
          </el-pagination>
        </div>
      </card>
    </div>
  </div>

</template>

<style scoped>
.ban{
  &:hover{
    cursor: pointer;
  }
}
</style>