<script lang="ts" setup>
import { Icon } from "@iconify/vue";

type PropsType = {
  iconCode: string;
};
const props = defineProps<PropsType>();
</script>
<template>
  <icon v-if="iconCode" :icon="iconCode"></icon>
</template>
