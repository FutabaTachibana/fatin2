<template>
  <el-form label-width="140px" v-loading="loading" class="config-form">
    <div v-for="item in config" :key="item.key">
      <el-form-item :label="item.label">
        <!-- String -->
        <el-input
          v-if="item.type === 'string'"
          v-model="formData[item.key]"
          :placeholder="item.description"
        />

        <!-- Number -->
        <el-input-number
          v-if="item.type === 'number'"
          v-model="formData[item.key]"
          :placeholder="item.description"
        />

        <!-- Boolean -->
        <el-switch
          v-if="item.type === 'boolean'"
          v-model="formData[item.key]"
        />

        <!-- Select -->
        <el-select
          v-if="item.type === 'select'"
          v-model="formData[item.key]"
          :placeholder="item.description"
        >
          <el-option
            v-for="opt in item.options"
            :key="opt"
            :label="opt"
            :value="opt"
          />
        </el-select>

        <div v-if="item.description" class="form-tip">{{ item.description }}</div>
      </el-form-item>
    </div>

    <el-form-item>
      <el-button type="primary" @click="submit" :loading="loading">保存修改</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  config: {
    type: Array,
    default: () => []
  },
  loading: Boolean
})

const emit = defineEmits(['save'])

const formData = reactive({})

// Initialize formData when config changes
watch(() => props.config, (newConfig) => {
  if (newConfig) {
    newConfig.forEach(item => {
      // Only set if not already present to avoid overwriting user edits if config reloads?
      // Actually we want to sync with upstream, so overwriting is usually correct on load.
      formData[item.key] = item.value
    })
  }
}, { immediate: true, deep: true })

const submit = () => {
  emit('save', formData)
}
</script>

<style scoped>
.config-form {
  max-width: 800px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
  margin-top: 4px;
  width: 100%;
}
</style>
