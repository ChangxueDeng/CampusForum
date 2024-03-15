<script setup>
import {logout} from "@/net/net.js";
import {router} from "@/router/index.js";
</script>

<template>
<div>
  <el-button @click="logout(()=> {router.push('/')})" plain type="primary"> 退出登陆</el-button>
</div>
</template>

<style scoped>

</style>