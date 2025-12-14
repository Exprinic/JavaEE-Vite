<template>
  <div>FAQ</div>
  <div>
    <div v-for="faq in faqs" :key="faq.id">
      <div>{{ faq.question }}</div>
      <div>{{ faq.answer }}</div>
    </div>
  </div>
  <div>
    <div v-for="faq in faqs" :key="faq.id">
      <div>{{ faq.question }}</div>
      <div>{{ faq.answer }}</div>
    </div>
  </div>
  <div>
    <div v-for="faq in faqs" :key="faq.id">
      <div>{{ faq.question }}</div>
      <div>{{ faq.answer }}</div>
    </div>
  </div>
</template>
<script setup>
import { useFaqStore } from '../stores/faqStore';
import { storeToRefs } from 'pinia';

const faqStore = useFaqStore();
const { faqs } = storeToRefs(faqStore);
</script>
<style scoped>
</style>