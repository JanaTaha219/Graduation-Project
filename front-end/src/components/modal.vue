<template>
  <div v-if="modalActive" class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <button @click="close" class="close-button">&times;</button>
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";

const props = defineProps({
  modalActive: {
    type: Boolean,
    default: false,
  },
  note: Object,
});

const emit = defineEmits(["close"]);

const close = () => {
  emit("close");
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  position: relative;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}
</style>
