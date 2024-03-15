<script setup>

import {Message, User,Lock} from "@element-plus/icons-vue";
import {router} from "@/router/index.js";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import { post} from "@/net/net.js";
//form
const form = reactive({
  username: '',
  password: '',
  passwd_repeat: '',
  email: '',
  email_code: '',
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
  }else if(value !== form.password){
    callback(new Error('两次密码不一致'))
  }else {
    callback()
  }
}
//rules
const rules = {
  username:[{validator: usernameValidate, trigger:['blur', 'change']},
    {min:3, max:6, message: '用户名为3到6个字符长度', trigger: ['blur', 'change']}],
  password:[{required: true, message:'请输入密码', trigger:['blur','change']},
    {min:6, message: '密码长度应大于6个字符', trigger: ['blur', 'change']}],
  passwd_repeat:[{validator: passwd_repeatValidate, trigger:['blur', 'change']}],
  email:[{required: true, message: '请输入电子邮箱', trigger:['blur', 'change']},
    {type: 'email', message: '请输入合法的邮件地址', trigger: ['blur','change']}],
  email_code:[{required:true, trigger:['blur','change'],message:'请输入验证码'},
    {min:6, max: 6, message: '验证码为6个字符', trigger: ['blur','change']}],
}

//验证码按钮状态
const isValidEmail = ref(false)

//校验邮箱是否填写，用于发送验证码码校验
const onValid = (prop, isValid)=>{
  if(prop === 'email'){
    isValidEmail.value = isValid
  }
}
//获取验证码限流
const coldTime = ref(0)

//表单完整填写检验
const formRef = ref()

const userRegister = ()=>{
  formRef.value.validate((isValid)=>{
    if(!isValid){
      ElMessage.warning('请完整填写信息')
    }else{
      //进行注册
      post('api/auth/register', {
        username: form.username,
        password: form.password,
        email: form.email,
        code: form.email_code
      }, ()=>{
        ElMessage.success('注册成功')
        router.push('/')
      }, (message)=>{
        ElMessage.warning(message)
      })
    }
  })
}

//验证邮箱,并发送邮件

const validateEmail = ()=>{
  coldTime.value = 60;
  post(`api/auth/ask-code?email=${form.email}&type=register`,
      {}, ()=>{
    ElMessage.success("发送验证码成功，请注意查收")
    setInterval(()=> coldTime.value--, 1000)
  }, (message)=>{
    ElMessage.warning(message)
    coldTime.value = 0;
  })
}


// const validateEmail = ()=>{
//   coldTime.value = 60; //限制频繁发送
//   post('',{
//     email: form.email
//   },(message)=>{
//     ElMessage.success(message)
//     setInterval(()=>coldTime.value = coldTime.value - 1, 1000)
//   }, (message)=>{
//     ElMessage.warning(message)
//     coldTime.value = 0
//   })
// }

</script>

<template>
  <div>
    <div>
      <div style="text-align: center">
        <h2 style="margin-top: 220px">注册</h2>
        <el-text size="large">欢迎你，新用户，请填写信息进行注册</el-text>
      </div>
    </div>
    <div style="margin: 20px">
      <el-form :model="form" :rules="rules" @validate="onValid" ref="formRef">
        <el-form-item prop="username">
          <el-input placeholder="用户名" :prefix-icon="User" type="text" v-model="form.username" maxlength="6"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input placeholder="密码" :prefix-icon="Lock" type="password" v-model="form.password" minlength="6"></el-input>
        </el-form-item>
        <el-form-item prop="passwd_repeat">
          <el-input placeholder="确认密码" :prefix-icon="Lock" type="password" v-model="form.passwd_repeat" minlength="6"></el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input placeholder="电子邮箱" :prefix-icon="Message" type="text" v-model="form.email"></el-input>
        </el-form-item>
        <el-row>
          <el-col :span="15">
            <el-form-item prop="email_code">
              <el-input  placeholder="验证码" v-model="form.email_code"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="7" style="margin-left: 15px">
            <el-button type="primary" plain @click="validateEmail()" :disabled="coldTime > 0 || !isValidEmail">{{coldTime > 0 ? '等待' + coldTime + '秒' : '发送验证码'}}
            </el-button>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div style="margin-top: 30px; text-align: center">
      <el-button plain type="success" style="width: 150px;" @click="userRegister">立即注册</el-button>
      <el-divider>
        <el-link @click="router.push('/')">已有账户，返回登陆</el-link>
      </el-divider>
    </div>
  </div>
</template>

<style scoped>

</style>