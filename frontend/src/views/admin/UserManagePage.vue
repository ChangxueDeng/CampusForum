<script setup>

import {useStore} from "@/store/index.js";
import {get, post} from "@/net/net.js";
import {reactive, ref} from "vue";
import Card from "@/components/Card.vue";
import {InfoFilled} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

const store = useStore()
const users = reactive({
  data: null,
  page: 1,
  loading: false,
  editor: false,
  banned: false
})
const add = reactive(  {
      username: '',
      password: '',
      password_repeat: '',
      email: '',
      role: 'user',
})
//validate
const usernameValidate = (rule, value, callback)=>{
  if(value === ''){
    callback(new Error('请输入用户名'))
  }else if(!/[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error('\'用户名不能包含特殊字符，只能是中文/英文\''))
  }else {
    callback()
  }
}

const passwd_repeatValidate = (rule, value, callback)=>{
  if(value === ''){
    callback(new Error('请确认密码'))
  }else if(value !== add.password){
    callback(new Error('两次密码不一致'))
  }else {
    callback()
  }
}
//rules
const rules = {
  username:[{validator: usernameValidate, trigger:['blur', 'change']},
    {required: true, min:3, max:6, message: '用户名为3到6个字符长度', trigger: ['blur']}],
  password:[{required: true, message:'请输入密码', trigger:['blur']},
    {min:6, message: '密码长度应大于8个字符', trigger: ['blur']}],
  password_repeat:[{validator: passwd_repeatValidate, trigger:['blur']}, {required: true}],
  email:[{required: true, message: '请输入电子邮箱', trigger:['blur']},
    {type: 'email', message: '请输入合法的邮件地址', trigger: ['blur']}],
}
const formRef = ref()
function getUsers(page) {
  users.page = page
  get(`/api/admin/all-user?page=${page}`, data => {
    users.data = data
    users.loading = true
  })
}
function init() {
  formRef.value.resetFields()
}
function addUser() {
  formRef.value.validate((isValid)=> {
    if (isValid) {
      post('api/admin/add-User', {
        username: add.username,
        password: add.password,
        email: add.email,
        role: add.role,
      }, ()=>{
        users.editor = false
        userAfter('add')
        ElMessage.success('新建成功')
      }, (message)=> {
        ElMessage.warning(message)
      })
    } else {
      ElMessage.warning('请完整填写信息')
    }
  })

}
function userAfter(type) {
  if (type === 'add') {
    getUsers(Math.floor(++store.admin.userCount / 10) + 1)
  } else if (type === 'delete') {
    getUsers(Math.floor(--store.admin.userCount / 10) + 1)
  }
}
function banUser(id, ban, finish) {
  users.banned = ban
  get(`api/admin/ban-User?id=${id}&ban=${ban}`, ()=> {
    users.banned = !ban
    ElMessage.success(ban ? '解封成功' : '封禁成功')
    finish()
  })
}
getUsers(1)
store.adminActivate('user')
</script>

<template>
    <div v-loading="!users.loading">
      <div style="display: flex; justify-content: space-between">
        <card style="text-align: center; width: 100px; font-size: 18px; font-weight: bold;display:flex;justify-content: center; align-items: center">
          <div>用户管理</div>
        </card>
        <card style="text-align: center; width: 300px; margin-left: 30px; display: flex;justify-content: space-between">
          <div style="margin-top: 5px">当前用户数量:{{store.admin.userCount}}</div>
          <div><el-button style="margin-left: 10px; width: 80px;" plain round type="warning" @click="users.editor = true">
            新增</el-button></div>
        </card>
      </div>
      <div style="margin-top: 10px">
        <card>
          <div style="overflow: hidden;">
            <div style="height: 620px">
              <el-table :data="users.data" width="100%" fit border table-layout="auto" height="100%" size="large" >
                <template #empty>
                  <el-empty description="暂无数据"></el-empty>
                </template>
                <el-table-column label="Id" prop="id"/>
                <el-table-column label="用户名" prop="username"/>
                <el-table-column label="角色" prop="role"/>
                <el-table-column label="邮箱" prop="email"/>
                <el-table-column label="头像" prop="avatar"/>
                <el-table-column label="性别" prop="gender">
                  <template #default="scope">
                    <el-tag v-if="scope.row.gender === 0" type="success">男</el-tag>
                    <el-tag v-else type="danger">女</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="电话" prop="phone"/>
                <el-table-column label="QQ" prop="qq"/>
                <el-table-column label="WX" prop="wx"/>
                <el-table-column label="个人简介" prop="desc"/>
                <el-table-column label="封禁" prop="ban" align="center">
                  <template #default="scoop">
                    <el-popconfirm cancel-button-text="取消"
                                   confirm-button-text="确定"
                                   :icon="InfoFilled"
                                   icon-color="#626AEF"
                                   @confirm="banUser(scoop.row.id, scoop.row.ban, ()=> {
                                     scoop.row.ban = users.banned
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
                :total="store.admin.userCount"
                :current-page="users.page"
                @current-change="getUsers"
                hide-on-single-page>
            </el-pagination>
          </div>
        </card>
      </div>
      <div>
        <el-drawer :model-value="users.editor" direction="btt" :size="370" :close-on-click-modal="false"
                   @close="users.editor = false" title="新建用户" @open="init()">
          <card>
            <el-form :model="add" :rules="rules" ref="formRef" label-position="left" label-width="auto">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="add.username" placeholder="请输入用户名"></el-input>
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="add.password" type="password" placeholder="请输入密码"></el-input>
              </el-form-item>
              <el-form-item label="确认密码" prop="password_repeat">
                <el-input v-model="add.password_repeat" type="password" placeholder="请输入密码"></el-input>
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="add.email" placeholder="请输入密码"></el-input>
              </el-form-item>
              <el-form-item label="角色" prop="role">
                <el-radio-group v-model="add.role">
                  <el-radio value="user">User</el-radio>
                  <el-radio value="admin">Admin</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-form>
            <div style="display: flex; justify-content: right">
              <el-button type="primary" @click="addUser()" plain>立即新建</el-button>
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
.ban{
  &:hover{
    cursor: pointer;
  }
}
</style>