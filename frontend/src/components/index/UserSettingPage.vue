<script setup>

import Card from "@/components/Card.vue";
import {Message, Notebook, User, Select, Refresh} from "@element-plus/icons-vue";
import {useStore} from "@/store/index.js";
import {computed} from "vue";

const store = useStore()
const registerTime = computed(()=> new Date(store.user.registerTime).toLocaleDateString())
</script>

<template>
  <div style="display: flex">
    <div class="settings-left">
      <card :icon="User" title="账户信息设置" desc="在这里编辑你的个人信息，并且可以在隐私设置里选择是否展示">
          <el-form label-position="top" style="margin: 0 10px 10px 10px">
            <el-form-item label="用户名">

            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group>
                <el-radio value="1">男</el-radio>
                <el-radio value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input></el-input>
            </el-form-item>
            <el-form-item label="QQ号">
              <el-input></el-input>
            </el-form-item>
            <el-form-item label="微信号">
              <el-input></el-input>
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input type="textarea" :rows="6"></el-input>
            </el-form-item>
            <div>
              <el-button type="primary" plain :icon="Select"> 保存用户信息</el-button>
            </div>
          </el-form>

      </card>
      <card :icon="Message" title="电子邮箱设置" desc="在这里可以修改账号绑定的电子邮箱地址" style="margin-top: 10px">
        <el-form style="margin: 0 10px 10px 10px" label-position="top">
          <el-form-item label="电子邮箱地址">
            <el-input></el-input>
          </el-form-item>
          <el-form-item>
            <el-row style="width: 100%">
              <el-col :span="18">
                <el-input placeholder="请输入获取的验证码"></el-input>
              </el-col>
              <el-col :span="5" style="margin-left: 15px">
                <el-button type="primary" plain>获取验证码</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <div>
            <el-button type="primary" :icon="Refresh" plain> 保存邮箱</el-button>
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
              eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.
              eyJuYW1lIjoidGVzdCIsImlkIjoxLCJleHAiOjE3MTE5NzAzNTcsImlhdCI6MTcxMTk2Njc1NywianRpIjoi
              ZDFkODczYzctMjRiYS00OGMxLWI0MmYtOGYzNmQ3MThiZjM2IiwiYXV0aG9yaXRpZXMiOlsiUk9
              MRV91c2VyIl19.g2GytSAUuBnGH2deYbdbF60zh4e1UyMZuyakDD1K0Oo
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