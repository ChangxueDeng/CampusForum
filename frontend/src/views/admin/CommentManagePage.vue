<script setup>
import {useStore} from "@/store/index.js";
import {InfoFilled} from "@element-plus/icons-vue";
import {reactive} from "vue";
import "@/components/Card.vue"
import Card from "@/components/Card.vue";
import {get} from "@/net/net.js";
import {ElMessage} from "element-plus";
const store = useStore();
const comments = reactive({
  data: null,
  loading: false,
  page: 1,
  banned: false,
})
store.adminActivate('comment')
function getComments(page) {
  comments.page = page
  get(`/api/admin/comments?page=${page}`, data => {
    comments.data = data
    comments.loading = true
  })
}
function banComment(id, ban, finish) {
  comments.banned = ban
  get(`api/admin/ban-comment?id=${id}&ban=${ban}`, ()=> {
    comments.banned = !ban
    ElMessage.success(ban ? '解封成功' : '封禁成功')
    finish()
  })
}
function deleteComment(id) {
  get(`api/admin/delete-comment?id=${id}`, ()=> {
    ElMessage.success('删除成功')
    getComments(comments.page)
  })
}
getComments(1)
</script>

<template>
  <div v-loading="!comments.loading">
    <div style="display: flex; justify-content: space-between">
      <card style="text-align: center; width: 100px; font-size: 18px; font-weight: bold;display:flex;justify-content: center; align-items: center">
        <div>评论管理</div>
      </card>
      <card style="text-align: center; width: 300px; margin-left: 30px;">
        <div style="margin-top: 5px">当前评论数量:{{store.admin.commentCount}}</div>
      </card>
    </div>
    <div style="margin-top: 10px">
      <card>
        <div style="overflow: hidden;">
          <div style="height: 620px">
            <el-table :data="comments.data" width="100%" fit border table-layout="auto" height="100%" size="large" >
              <template #empty>
                <el-empty description="暂无数据"></el-empty>
              </template>
              <el-table-column label="Id" prop="id"/>
              <el-table-column label="帖子Id" prop="tid"/>
              <el-table-column label="用户Id" prop="uid"/>
              <el-table-column label="内容" prop="content"/>
              <el-table-column label="创建时间" prop="time"/>
              <el-table-column label="评论引用" prop="quote"/>
              <el-table-column label="封禁" prop="ban" align="center">
                <template #default="scoop">
                <el-popconfirm cancel-button-text="取消"
                               confirm-button-text="确定"
                               :icon="InfoFilled"
                               icon-color="#626AEF"
                               @confirm="banComment(scoop.row.id, scoop.row.ban, ()=> {
                                 scoop.row.ban = comments.banned
                               })"
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
              <el-table-column align="center">
                <template #default="scoop">
                  <el-popconfirm cancel-button-text="取消"
                                 confirm-button-text="确定"
                                 :icon="InfoFilled"
                                 icon-color="#626AEF"
                                 @confirm="deleteComment(scoop.row.id)"
                                 title="确定删除吗?">
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
      <card style="margin-top: 10px; height: 50px;">
        <div style="width: fit-content; margin: 0 auto">
          <el-pagination
              background
              layout="prev, pager, next"
              :page-size="10"
              :total="store.admin.commentCount"
              :current-page="comments.page"
              @current-change="getComments"
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