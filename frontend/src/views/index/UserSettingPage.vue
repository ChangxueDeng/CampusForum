<script setup>

import Card from "@/components/Card.vue";
import {Message, Notebook, User, Select, Refresh} from "@element-plus/icons-vue";
import {useStore} from "@/store/index.js";
import {computed, reactive, ref} from "vue";
import {get, post} from "@/net/net.js";
import {ElMessage} from "element-plus";

const store = useStore()
const registerTime = computed(()=> new Date(store.user.registerTime).toLocaleDateString())

const baseFormRef = ref()
const emailFormRef = ref()
const desc = ref()
//基本信息表单
const baseForm = reactive({
  username: '',
  gender: 0,
  phone: '',
  qq: '',
  wx: '',
  desc: ''
})

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error("请输入用户名"))
  } else if (!/[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('\'用户名不能包含特殊字符，只能是中文/英文\''))
  } else {
    callback()
  }
}

const rule = {
  username:[
    {validator: validateUsername, trigger: ['blur', 'change']},
    {min: 2, max: 10, message: '用户名必须在2-10个字符之间', trigger: ['blur', 'change']}],
  email:[{required: true, message: '请输入电子邮箱', trigger:['blur', 'change']},
    {type: 'email', message: '请输入合法的邮件地址', trigger: ['blur','change']}],
  code:[{required:true, trigger:['blur','change'],message:'请输入验证码'},
    {min:6, max: 6, message: '验证码为6个字符', trigger: ['blur','change']}],
}

//邮箱信息表单
const emailForm = reactive({
  email: '',
  code: ''
})

const loading = reactive({
  form: true,
  base: false
})

function savaDetails() {
  baseFormRef.value.validate(isValid => {
    if (isValid) {
      loading.base = true
      post('api/user/sava-details', baseForm, ()=> {
        ElMessage.success("用户信息保存成功")
        store.user.username = baseForm.username
        desc.value = baseForm.desc
        loading.base = false
      }, (message) =>{
        ElMessage.warning(message)
        loading.base = false
      })
    }
  })
}

const coldTime = ref(0)
const isValidEmail = ref(false)
const onValidate = (prop, isValid) => {
  if (prop === 'email') {
    isValidEmail.value = isValid
  }
}
function sendEmailCode(){
    coldTime.value = 60;
    post(`api/auth/ask-code?&email=${emailForm.email}&type=modify`,{},()=>{
      ElMessage.success(`验证码已发送至指定邮箱:${emailForm.email}，请注意查收`)
      const timer = setInterval(()=> {
        coldTime.value--
        if(coldTime.value === 0) {
          clearInterval(timer)
        }
      }, 1000)
    }, (message)=> {
      ElMessage.warning(message)
      coldTime.value = 0
    })
}

function modifyEmail(){
  emailFormRef.value.validate((isValid)=>{
    if (isValid) {
      post('api/user/modify-email', emailForm, ()=>{
        ElMessage.success("电子邮箱修改成功")
        store.user.email = emailForm.email;
        emailForm.code = ''
      })
    }
  })
}
get('api/user/details', (data)=> {
  baseForm.username = store.user.username
  baseForm.phone = data.phone
  baseForm.qq = data.qq
  baseForm.wx = data.wx
  desc.value = data.desc
  baseForm.desc = data.desc
  baseForm.gender = data.gender
  emailForm.email = store.user.email
  loading.form = false
})
</script>

<template>
  <div style="display: flex; max-width: 950px;margin: auto">
    <div class="settings-left">
      <card :icon="User" title="账户信息设置" desc="在这里编辑你的个人信息，并且可以在隐私设置里选择是否展示" v-loading="loading.form">
          <el-form label-position="top" style="margin: 0 10px 10px 10px" :model="baseForm" :rules="rule" ref="baseFormRef">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="baseForm.username" maxlength="10"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="baseForm.gender">
                <el-radio :value="0">男</el-radio>
                <el-radio :value="1">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="baseForm.phone" maxlength="11" minlength="11"></el-input>
            </el-form-item>
            <el-form-item label="QQ号" prop="qq">
              <el-input v-model="baseForm.qq" maxlength="13"></el-input>
            </el-form-item>
            <el-form-item label="微信号" prop="wx">
              <el-input v-model="baseForm.wx" maxlength="20"></el-input>
            </el-form-item>
            <el-form-item label="个人简介" prop="desc">
              <el-input v-model="baseForm.desc" type="textarea" :rows="6" maxlength="200"></el-input>
            </el-form-item>
            <div>
              <el-button type="primary" plain :icon="Select" @click="savaDetails()" :loading="loading.base"> 保存用户信息</el-button>
            </div>
          </el-form>

      </card>
      <card :icon="Message" title="电子邮箱设置" desc="在这里可以修改账号绑定的电子邮箱地址" style="margin-top: 10px" v-loading="loading.form">
        <el-form style="margin: 0 10px 10px 10px" label-position="top" :model="emailForm" :rules="rule" ref="emailFormRef" @validate="onValidate">
          <el-form-item label="电子邮箱地址" prop="email">
            <el-input v-model="emailForm.email"></el-input>
          </el-form-item>
            <el-row style="width: 100%">
              <el-col :span="18">
                <el-form-item prop="code">
                  <el-input placeholder="请输入获取的验证码" v-model="emailForm.code"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="5" style="margin-left: 15px">
                <el-button type="primary" plain @click="sendEmailCode()" :disabled="!isValidEmail ||coldTime > 0">{{coldTime > 0 ?
                    '请等待：' + coldTime + '秒' : '获取验证码'}}</el-button>
              </el-col>
            </el-row>
          <div>
            <el-button type="primary" :icon="Refresh" plain @click="modifyEmail()"> 保存邮箱</el-button>
          </div>
        </el-form>
      </card>
    </div>
    <div class="settings-right">
      <div style="position: sticky;top: 20px">
        <card>
          <div style="text-align: center; padding: 5px 15px 0 15px;">
            <el-avatar :size="60" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
            <div style="font-weight: bold">你好, {{store.user.username}}</div>
          </div>
          <el-divider style="margin: 10px 0;"></el-divider>
          <div>
            <el-text style="font-size: 14px; color: gray; padding: 10px">
              {{desc || '个人简介未填写'}}
            </el-text>
          </div>
        </card>
        <card style="margin-top: 10px; font-size: 14px">
          <div style="font-weight: bold">
            账号注册时间: {{registerTime}}
          </div>
          <div style="color: gray">
            欢迎使用CampusForum!!
          </div>

        </card>
      </div>

    </div>
  </div>
</template>

<style scoped>
.settings-left{
  flex: 1;
  margin: 20px;
}
.settings-right{
  width: 300px;
  margin: 20px 20px 20px 0;
}
</style>