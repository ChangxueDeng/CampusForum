<script setup>

import {Link, Lock, User} from "@element-plus/icons-vue";
import {router} from "@/router/index.js";
import {reactive, ref} from "vue";
import {login} from "@/net/net.js";
import  {ElMessage} from "element-plus";

const form = reactive({
  username: '',
  password: '',
  remember: false
})

const rule = {
  username: {required: true, message: '请输入用户名', trigger:['blur', 'change']},
  password: {required: true, message: '请输入密码', trigger: ['blur', 'change']}
}

function testAccount(){
  form.username = "test"
  form.password = "12345678"
}
// const login = ()=>{
//   if(!form.username || !form.password){
//    ElMessage.warning("请填写用户名和密码")
//   }else {
//     post("/api/auth/login",{
//       username: form.username,
//       password: form.password,
//       remember: form.remember,
//     }, (message)=>{
//         ElMessage.success(message)
//         router.push('/index')
//     }, (message)=>{
//       ElMessage.error(message)
//     })
//   }
// }

const formRef = ref()

function userLogin(){
  formRef.value.validate((isValid)=>{
    if(!isValid){
      ElMessage.warning('请完整填写信息')
    }else {
      login(form.username, form.password, form.remember, ()=>{})
    }
  })
}
</script>

<template>
  <div>
    <div>
      <div style="text-align: center">
        <h2 style="margin-top: 220px">登陆</h2>
        <el-text size="large">欢迎进入论坛，请输入用户名和密码进行登陆</el-text>
      </div>
    </div>
    <div style="margin-top: 20px">
      <el-form style="margin: 20px;" :model="form" ref="formRef" :rules="rule">
        <el-form-item prop="username">
          <el-input placeholder="用户名" :prefix-icon="User" v-model="form.username" minlength="2" maxlength="10"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input placeholder="密码" type="password" :prefix-icon="Lock" v-model="form.password" minlength="6" maxlength="16"></el-input>
        </el-form-item>
      </el-form>
      <div style="text-align: center; margin-top: 30px" >
        <el-row style="margin: 20px">
          <el-col style="text-align: left" :span="12">
            <el-checkbox v-model="form.remember" label="记住我"></el-checkbox>
          </el-col>
          <el-col style="text-align: right;" :span="12">
            <el-link @click="router.push('/forget')">忘记密码？</el-link>
          </el-col>
        </el-row>
        <el-button plain type="success" style="width: 190px;" @click="userLogin()" >立即登陆</el-button>
      </div>
    </div>
    <div>
      <el-divider >
        <el-text style="font-size: 14px">没有账户,</el-text>
        <el-link @click="router.push('/register')" style="margin-top: -3px; font-size: 15px" :icon="Link">进行注册</el-link>
      </el-divider>
    </div>
    <div style="text-align: center">
      <el-button @click="testAccount">使用测试账号</el-button>
    </div>
  </div>

</template>

<style scoped>

</style>