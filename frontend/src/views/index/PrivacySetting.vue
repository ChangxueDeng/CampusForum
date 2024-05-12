<script setup>

import Card from "@/components/Card.vue";
import {Lock, Setting, Switch} from "@element-plus/icons-vue";
import {reactive,ref} from "vue";
import {post, get} from "@/net/net.js";
import {ElMessage} from "element-plus";
import {useStore} from "@/store/index.js";

const form = reactive({
  password: '',
  new_password:'',
  new_repeat_password:'',
})
const formRef = ref()

const validatePassword = (rule, value, callback)=>{
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== form.new_password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  password:[{
    required:true, message:'请输入原来的密码', trigger:'blur'
  }],
  new_password:[
      {required: true, message:'请输入新的密码', trigger:['blur', 'change']},
    {min: 6, max: 16, message: '密码长度在6-16个字符之间', trigger: ['blur', 'change']}],
  new_repeat_password:[
    {required: true, validator: validatePassword, trigger: ['blur', 'change']}
  ]
}

const valid = ref(false)
const onValidate = (prop, isValid) => valid.value = isValid;

function resetPassword(){
  formRef.value.validate(isValid=> {
    if(isValid) {
      post('api/user/change-password', {
        password: form.password,
        newPassword: form.new_password,
      }, ()=>{
        ElMessage.success("修改密码成功")
        formRef.value.resetFields()
      }, (message) => {
        ElMessage.warning(message)
        formRef.value.resetFields()
      })
    }
  })
}
const privacy = reactive({
  gender: false,
  phone: false,
  qq: false,
  wx: false,
  email: false,
})
const saving = ref(true)

get('api/user/privacy', data => {
  privacy.gender = data.gender
  privacy.phone = data.phone
  privacy.qq = data.qq
  privacy.wx = data.wx
  privacy.email = data.email
  saving.value = false;
})

function savaPrivacy(type, status){
  saving.value = true;
  post('api/user/sava-privacy',{
    type: type,
    status: status
  } ,()=>{
    ElMessage.success("隐私设置修改成功")
    saving.value = false;
  })
}
const store = useStore()
store.forumActivate(4)
</script>

<template>
  <div style="margin: auto; max-width: 600px">
    <div style="margin-top: 20px">
      <card :icon="Setting" title="隐私设置" desc="在这里设置哪些内容可以被别人看到" v-loading="saving">
        <div class="checkbox-list">
          <el-checkbox @change="savaPrivacy('gender', privacy.gender)"
          v-model="privacy.gender">公开展示我的性别</el-checkbox>
          <el-checkbox @change="savaPrivacy('phone', privacy.phone)"
          v-model="privacy.phone">公开展示我的手机号</el-checkbox>
          <el-checkbox @change="savaPrivacy('qq', privacy.qq)"
          v-model="privacy.qq">公开展示我的QQ号</el-checkbox>
          <el-checkbox @change="savaPrivacy('wx', privacy.wx)"
          v-model="privacy.wx">公开展示我的微信号</el-checkbox>
          <el-checkbox @change="savaPrivacy('email', privacy.email)"
          v-model="privacy.email">公开展示我的电子邮箱地址</el-checkbox>
        </div>

      </card>
      <card style="margin-top: 20px" :icon="Setting" title="修改密码" desc="在此处修改密码，请务必牢记您的密码">
        <el-form label-width="100" style="margin: 20px;" :model="form" :rules="rules" ref="formRef" @validate="onValidate">
          <el-form-item label="当前密码" prop="password">
            <el-input type="password" :prefix-icon="Lock" placeholder="当前密码" maxlength="16" minlength="6" v-model="form.password"></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="new_password">
            <el-input type="password" :prefix-icon="Lock" placeholder="新密码" maxlength="16" minlength="6" v-model="form.new_password"></el-input>
          </el-form-item>
          <el-form-item label="确认新密码" prop="new_repeat_password">
            <el-input type="password" :prefix-icon="Lock" placeholder="确认新密码" maxlength="16"  minlength="6" v-model="form.new_repeat_password"></el-input>
          </el-form-item>
        </el-form>
        <div style="text-align: center">
          <el-button  type="success" plain :icon="Switch" :disabled="!valid" @click="resetPassword()">立即重置密码</el-button>
        </div>
      </card>
    </div>
  </div>
</template>

<style scoped>
.checkbox-list{
  margin: 10px 0 0 10px;
  display: flex;
  flex-direction: column;
}
</style>