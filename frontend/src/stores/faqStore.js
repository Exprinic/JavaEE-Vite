import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useFaqStore = defineStore('faq', () => {
  const faqs = ref([
    {id: 1, question: 'What is EAZ?', answer: 'EAZ is a platform for students to find and connect with each other.'},
    {
      id: 2,
      question: 'How do I register?',
      answer: 'You can register on the platform by clicking on the "Register" button.'
    },
    {
      id: 3,
      question: 'What are the features of EAZ?',
      answer: 'EAZ offers features such as course discovery, discussion forums, and a community of students.'
    },
  ]);

  return {
    faqs,
  };
});