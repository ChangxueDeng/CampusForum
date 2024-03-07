<script setup>
import {logout} from "@/net/net.js";
</script>

<template>
<div>
  <el-button @click="logout()" plain type="primary"> 退出登陆</el-button>
</div>
</template>

<style scoped>

</style>