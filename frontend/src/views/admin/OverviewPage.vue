<script setup>
import Card from "@/components/Card.vue";
import {Bell, ChatLineSquare, EditPen, Notification, UserFilled} from "@element-plus/icons-vue";
import {useStore} from "@/store/index.js";
import ColorDot from "@/components/ColorDot.vue";
import {ref} from "vue";
const store = useStore();
store.adminActivate('overview')
const loading = ref(false)
function init() {
  if (store.admin) {
    loading.value = true
  }
}
init()
</script>

<template>
  <div v-loading="!loading">
    <div class="static">
      <card>
        <el-row>
          <el-col :span="8">
            <card style="display: flex; margin-top: 10px;">
              <el-icon style="font-size: 40px; translate: 0 6px"><el-icon><UserFilled/></el-icon></el-icon>
              <el-statistic :value="store.admin.userCount" value-style="font-size: 26px">
                <template #title>
                    <el-tag type="info">
                      <el-text size="large" tag="b">用户数量</el-text>
                    </el-tag>
                </template>
              </el-statistic>
            </card>
          </el-col>
          <el-col :span="8">
            <card style="display: flex; margin-top: 10px;margin-left: 10px">
              <el-icon style="font-size: 40px;translate: 0 6px"><ChatLineSquare/></el-icon>
              <el-statistic title="评论数量" :value="store.admin.commentCount" value-style="font-size: 26px">
                <template #title>
                  <el-tag type="warning">
                    <el-text size="large" tag="b">评论数量</el-text>
                  </el-tag>
                </template>
              </el-statistic>
            </card>
          </el-col>
          <el-col :span="8">
            <card style="display: flex; margin-top: 10px;margin-left: 10px">
              <el-icon style="font-size: 40px;translate: 0 6px"><Bell /></el-icon>
              <el-statistic title="通知数量" :value="store.admin.notificationCount" value-style="font-size: 26px">
                <template #title>
                  <el-tag type="success">
                    <el-text size="large" tag="b">通知数量</el-text>
                  </el-tag>
                </template>
              </el-statistic>
            </card>
          </el-col>
        </el-row>
          <card style="margin-top: 10px; display: flex; flex-direction: column">
            <div style="display: flex; margin-right: 20px">
              <el-icon style="font-size: 40px; translate: 0 6px; padding-top: 8px"><el-icon><Notification/></el-icon></el-icon>
              <el-statistic :value="store.admin.announcementCount" value-style="font-size: 26px">
                <template #title>
                  <el-tag>
                    <el-text size="large" tag="b">公告数量</el-text>
                  </el-tag>
                </template>
              </el-statistic>
            </div>
            <el-divider style="margin: 5px 0"></el-divider>
            <div style="display: flex; flex-direction: column;">
              <div>
                <el-text size="large" tag="b">最新公告：</el-text>
              </div>
              <div style="margin-top: 5px">
                <el-tag size="large">标题</el-tag>
                <el-tag size="large" effect="plain" style="margin-left: 5px">{{store.admin.announcement?.title}}</el-tag>
              </div>
              <div style="margin-top: 5px">
                <el-tag size="large">内容</el-tag>
                <el-tag size="large" effect="plain" style="margin-left: 5px">{{store.admin.announcement?.content}}</el-tag>
              </div>
              <div style="margin-top: 5px">
                <el-tag size="large">发布时间</el-tag>
                <el-tag size="large" effect="plain" style="margin-left: 5px">{{store.admin.announcement?.time}}</el-tag>
              </div>
            </div>
        </card>
        <card style="display: flex; margin-top: 10px; ">
          <el-icon style="font-size: 40px; translate: 0 6px"><EditPen/></el-icon>
          <el-statistic title="主题贴数量" :value="store.admin.topicCount" value-style="font-size: 26px">
            <template #title>
              <el-tag type="danger">
              <el-text size="large" tag="b">主题贴数量</el-text>
            </el-tag>
            </template>
          </el-statistic>
        </card>
        <card style="display: flex; justify-content: center; margin-top: 2px">
          <card v-for="(type, index) in store?.forum.types" :style="{ border: `1px solid ${type.color}`, 'margin-left' : '5px'}">
            <color-dot :color="type.color"></color-dot>
            <el-text>{{type.name}}类型: {{index === 0 ? store.admin.topicCount : store.admin.topicCountByType[index - 1]}}</el-text>
          </card>
        </card>
      </card>
    </div>
  </div>


</template>

<style scoped>
.static{
  margin: 100px auto;
  justify-content: center;
  height: 500px;
  width: 900px;
}
</style>