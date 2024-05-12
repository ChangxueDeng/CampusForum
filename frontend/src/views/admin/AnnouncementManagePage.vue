<script setup>
import {useStore} from "@/store/index.js";
import {reactive, ref} from "vue";
import {InfoFilled} from "@element-plus/icons-vue";
import Card from "@/components/Card.vue";
import {get, post} from "@/net/net.js";
import {ElMessage} from "element-plus";

const announcements = reactive({
  data: null,
  loading: false,
  page: 1,
  editor: false,
  editorType: 0,
})

const ann = reactive({
  id: 1,
  title: "",
  content: "",
})
const store = useStore()
store.adminActivate("announcement")

const formRef = ref()

const rule = {
  title: [
    {required: true, message: "标题不能为空", trigger: "blur"},
  ],
  content: [
    {required: true, message: "内容不能为空", trigger: "blur"},
  ]
}
function getAnnouncements(page) {
  announcements.page = page
  get(`/api/admin/announcements?page=${page}`, data => {
    announcements.data = data
    announcements.loading = true
  })
}
function addAnnouncement() {
  post('api/admin/add-announcement', {
    title : ann.title,
    content: ann.content
  },()=> {
    announcementAfter('add')
    ElMessage.success("增加成功")
  })
}
function announcementAfter(type) {
  if (type === 'add') {
    getAnnouncements(Math.floor(++store.admin.announcementCount / 10) + 1)
    announcements.editor = false
  } else if (type === 'delete') {
    getAnnouncements(Math.floor(--store.admin.announcementCount / 10) + 1)
  }
}
function editAnnouncement(row) {
  if (row) {
    ann.id = row.id
    ann.title = row.title
    ann.content = row.content
    announcements.editorType = 1
  } else {
    ann.title = ""
    ann.content = ""
  }
}
function deleteAnnouncement(id) {
  get(`/api/admin/delete-announcement?id=${id}`,()=> {
    announcementAfter('delete')
    ElMessage.success("删除成功")
  })
}
function updateAnnouncement() {
  formRef.value.validate((isValid) => {
    if (isValid) {
      post(`/api/admin/update-announcement`, {
        id : ann.id,
        title: ann.title,
        content: ann.content,
      }, ()=> {
        announcements.editor = false
        ElMessage.success("修改成功")
      })
    } else {
      ElMessage.warning("请完整填写信息")
    }
  })

}
getAnnouncements(1)
</script>

<template>
  <div v-loading="!announcements.loading">
    <div style="display: flex; justify-content: space-between">
      <card style="text-align: center; width: 100px; font-size: 18px; font-weight: bold;display:flex;justify-content: center; align-items: center">
        <div>公告管理</div>
      </card>
      <card style="text-align: center; width: 300px; margin-left: 30px; display: flex;justify-content: space-between">
        <div style="margin-top: 5px">当前公告数量:{{store.admin.announcementCount}}</div>
        <div><el-button style="margin-left: 10px; width: 80px;" plain round type="warning"
                        @click="announcements.editor = true; editAnnouncement(); ">
          新增</el-button>
        </div>
      </card>
    </div>
    <div style="margin-top: 10px">
      <card>
        <div style="overflow: hidden;">
          <div style="height: 600px">
            <el-table :data="announcements.data" width="100%" fit border table-layout="auto" height="100%" size="large">
              <template #empty>
                <el-empty description="暂无数据"></el-empty>
              </template>
              <el-table-column label="Id" prop="id"/>
              <el-table-column label="创建者" prop="uid"/>
              <el-table-column label="标题" prop="title"/>
              <el-table-column label="内容" prop="content"/>
              <el-table-column label="创建时间" prop="time"/>
              <el-table-column >
                <template #default = "scoop">
                  <el-popconfirm cancel-button-text="取消"
                                 confirm-button-text="确定"
                                 :icon="InfoFilled"
                                 @confirm="deleteAnnouncement(scoop.row.id)"
                                 icon-color="#626AEF"
                                 title="确定删除吗?">
                    <template #reference>
                      <el-button type="danger">删除</el-button>
                    </template>
                  </el-popconfirm>
                  <el-button type="success" @click="announcements.editor = true; editAnnouncement(scoop.row)">修改</el-button>
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
              :total="store.admin.announcementCount"
              :current-page="announcements.page"
              @current-change="getAnnouncements"
              hide-on-single-page>
          </el-pagination>
        </div>
      </card>
    </div>
    <div>
      <el-drawer :model-value="announcements.editor" direction="btt" :size="270" :close-on-click-modal="false"
                 @close="announcements.editor = false" :title="announcements.editorType === 0 ? '新增公告' : '修改公告'">
        <card>
          <el-form :model="ann" :rules="rule" ref="formRef">
            <el-form-item label="标题" prop="title">
              <el-input v-model="ann.title" placeholder="请输入标题"></el-input>
            </el-form-item>
            <el-form-item label="内容" prop="content">
              <el-input v-model="ann.content" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
          </el-form>
          <div style="display: flex; justify-content: right">
            <el-button  @click="announcements.editorType === 0? addAnnouncement() : updateAnnouncement()" plain>
              {{announcements.editorType === 0 ? '立即新增' : '立即修改'}}
            </el-button>
          </div>
        </card>
      </el-drawer>
    </div>
  </div>
</template>

<style scoped>
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