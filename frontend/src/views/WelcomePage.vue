<script setup>

</script>

<template>
  <div style="width:100vw; height: 100vh ;overflow: hidden ;display: flex">
    <div style="flex: 1">
      <el-image style="width: 100%; height: 100%" src="src/assets/app.jpg"></el-image>
    </div>
    <div class="card">
      <router-view v-slot="{Component}">
        <transition name="el-zoom-in-center" mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<style scoped>
.card{
  width: 350px;
  z-index: 1;
  background-color: var(--el-bg-color);
}
body{
  margin: 0;
}
</style>