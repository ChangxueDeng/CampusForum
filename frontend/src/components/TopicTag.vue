<script setup>
import {useStore} from "@/store/index.js";

const store = useStore()

defineProps({
  type: Number
})
</script>

<template>
    <div class="topic-type" :style="{
              color: store.findTyById(type)?.color + 'EE',
              'border-color': store.findTyById(type)?.color + '77',
              'background': store.findTyById(type)?.color + '33'
              }">
      {{store.findTyById(type)?.name}}
    </div>
</template>

<style scoped>
.topic-type{
  display: inline-block;
  border: solid 0.5px grey;
  border-radius: 5px;
  font-size: 12px;
  padding: 0 5px;
  height: 18px;
}
</style>