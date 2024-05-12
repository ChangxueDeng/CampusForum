<script setup>
import {useStore} from "@/store/index.js";
import {reactive} from "vue";
import Card from "@/components/Card.vue";
import {get} from "@/net/net.js"
import {InfoFilled} from "@element-plus/icons-vue";
const store = useStore()
const notice = reactive({
  loading: false,
  data: null,
  page: 1
})
store.adminActivate('notice')
function getNotice(page) {
  notice.page = page
  get(`/api/admin/notifications?page=${page}`, (data) => {
    notice.data = data
    notice.loading = true
  })
}
getNotice(1)
</script>

<template>
  <div v-loading="!notice.loading">
    <div style="display: flex; justify-content: space-between">
      <card style="text-align: center; width: 100px; font-size: 18px; font-weight: bold;display:flex;justify-content: center; align-items: center">
        <div>通知管理</div>
      </card>
      <card style="text-align: center; width: 300px; margin-left: 30px;">
        <div style="margin-top: 5px">当前通知数量:{{store.admin.notificationCount}}</div>
      </card>
    </div>
    <div style="margin-top: 10px">
      <card>
        <div style="overflow: hidden;">
          <div style="height: 600px">
            <el-table :data="notice.data" width="100%" fit border table-layout="auto" height="100%" size="large">
              <template #empty>
                <el-empty description="暂无数据"></el-empty>
              </template>
              <el-table-column label="Id" prop="id"/>
              <el-table-column label="用户" prop="uid"/>
              <el-table-column label="标题" prop="title"/>
              <el-table-column label="内容" prop="content"/>
              <el-table-column label="类型" prop="type"/>
              <el-table-column label="链接" prop="url"/>
              <el-table-column label="创建时间" prop="time"/>
              <el-table-column >
                <template #default>
                  <el-popconfirm cancel-button-text="取消"
                                 confirm-button-text="确定"
                                 :icon="InfoFilled"
                                 icon-color="#626AEF"
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
              :total="store.admin.notificationCount"
              :current-page="notice.page"
              @current-change="getNotice"
              hide-on-single-page>
          </el-pagination>
        </div>
      </card>
    </div>
  </div>
</template>

<style scoped>

</style>