<script setup>

import {reactive, ref} from "vue";
import {Lock, Message} from "@element-plus/icons-vue";
import {router} from "@/router/index.js";
import {post} from "@/net/net.js";
import {ElMessage} from "element-plus";



const activate = ref(1)

//form
const form = reactive({
  password: '',
  passwd_repeat: '',
  email: '',
  email_code: '',
})
//validate
const passwd_repeatValidate = (rule, value, callback)=>{
  if(value === ''){
    callback(new Error('请确认密码'))
  }else if(value !== form.password){
    callback(new Error('两次密码不一致'))
  }else {
    callback()
  }
}

//发送验证码按钮状态
const isValidEmail = ref(false)

//验证邮箱是否填写
const onValid = (prop, isValid)=>{
  if(prop === 'email'){
    isValidEmail.value = isValid
  }
}
//发送按钮限流
const coldTime = ref(0)

//验证邮箱并发送验证码
const validateEmail = ()=>{
  coldTime.value = 60;
  post(`api/auth/ask-code?email=${form.email}&type=reset`,{}, ()=>{
    ElMessage.success('发送验证码成功，请注意查收')
    setInterval(()=> coldTime.value--, 1000)
  }, (message)=>{
    ElMessage.warning(message)
    coldTime.value = 0;
  })
}

//验证完整
const formRef = ref()

//进入重置
const toForget = ()=>{
  formRef.value.validate((isValid)=>{
    if(!isValid){
      ElMessage.warning('请完整填写信息')
    }else {
      post('api/auth/reset-confirm',{
        email: form.email,
        code: form.email_code
      }, ()=>{
        activate.value = 2
      }, (message)=>{
        ElMessage.warning(message)
      })
    }
  })
}

const formRefDo = ref()

//进行重置
const doForget = ()=>{
  formRefDo.value.validate((isValid)=>{
    if(!isValid){
      ElMessage.warning('请完整填写信息')
    }else{
      post( 'api/auth/reset-password'
          , {
            email: form.email,
            code: form.email_code,
            password: form.password
          },()=>{
            ElMessage.success('密码重置成功')
            router.push('/')
          }, (message)=>{
            ElMessage.warning(message)
          })
    }
  })
}

//rules
const rules = {
  email: [{required: true, message: '请输入电子邮箱', trigger:['blur','change']},
    {type: 'email', message: '请输入合法邮件地址', trigger: ['blur', 'change']}],
  email_code: {required:true, message: '请输入获取的验证码', trigger:['blur', 'change']},
  password:[{required: true, message:'请输入密码', trigger:['blur','change']},
    {min:8, message: '密码长度应大于6个字符', trigger: ['blur', 'change']}],
  passwd_repeat:[{validator: passwd_repeatValidate, trigger:['blur', 'change']}],
}
</script>

<template>
  <div >
    <div style="margin-top: 30px">
      <el-steps :active="activate" align-center>
        <el-step title="Step 1" #description>
          <el-text>验证邮箱</el-text>
        </el-step>
        <el-step title="Step 2" #description >
          <el-text>重置密码</el-text>
        </el-step>
      </el-steps>
    </div>
    <div style="text-align: center">
      <h2 style="margin-top: 90px">{{activate === 1 ? "验证邮箱" : "重置密码"}}</h2>
    </div>
    <div>
      <transition name="el-zoom-in-top" mode="out-in" style="position: absolute">
        <div style="text-align: center" v-show="activate=== 1 ">
          <el-text>请填写邮箱获取验证码后进行验证</el-text>
          <el-form style="margin: 20px;" :model="form" ref="formRef" @validate="onValid" :rules="rules">
            <el-form-item prop="email">
              <el-input :prefix-icon="Message" placeholder="邮箱" v-model="form.email"></el-input>
            </el-form-item>
            <el-row>
              <el-col :span="15">
                <el-form-item prop="email_code">
                  <el-input placeholder="验证码" :prefix-icon="Lock" v-model="form.email_code" minlength="6" maxlength="6"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="7" style="margin-left: 14px">
                <el-button plain type="primary" :disabled="!isValidEmail || coldTime > 0" @click="validateEmail">{{coldTime > 0 ? '请等待' + coldTime + '秒' : '获取验证码'}}</el-button>
              </el-col>
            </el-row>
          </el-form>
          <el-button style="width: 150px; margin-top: 30px" plain type="success" @click="toForget">验证邮箱</el-button>
          <div style="margin-top: 40px;" >
            <el-divider>
              <el-link @click="router.push('/')" style="font-size: 16px">返回登陆页面</el-link>
            </el-divider>
          </div>
        </div>
      </transition>
      <transition name="el-zoom-in-top" mode="out-in" >
        <div style="text-align: center" v-show="activate=== 2 ">
          <el-text>请填写密码进行重置</el-text>
          <div style="margin-top: 20px">
            <el-form style="margin: 20px" :model="form" ref="formRefDo" :rules="rules">
              <el-form-item prop="password">
                <el-input type="password" :prefix-icon="Lock" placeholder="密码" v-model="form.password" minlength="6" maxlength="16"></el-input>
              </el-form-item>
              <el-form-item prop="passwd_repeat">
                <el-input type="password" :prefix-icon="Lock" placeholder="确认密码" v-model="form.passwd_repeat" minlength="6" maxlength="16"></el-input>
              </el-form-item>
            </el-form>
          </div>
          <el-button plain style="width: 150px; margin-top: 20px" type="warning" @click="doForget">立即重置</el-button>
          <div style="margin-top: 40px;" >
            <el-divider>
              <el-link @click="router.push('/')" style="font-size: 16px">返回登陆页面</el-link>
            </el-divider>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<style scoped>

</style>