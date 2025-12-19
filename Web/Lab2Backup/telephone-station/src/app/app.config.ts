// src/app/app.config.ts

import { ApplicationConfig, importProvidersFrom } from '@angular/core'; // importProvidersFrom теперь не нужен, можно убрать
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';

import { initializeApp, provideFirebaseApp } from '@angular/fire/app';
import { getFirestore, provideFirestore } from '@angular/fire/firestore';

const firebaseConfig = { 
  projectId: "tttt-560ce", 
  appId: "1:87415914500:web:bca56b3168a7689fbcf551", 
  databaseURL: "https://tttt-560ce.firebaseio.com", 
  storageBucket: "tttt-560ce.appspot.com", 
  apiKey: "AIzaSyDXde3cm6X5D9WvKahIWHtkI1TKcKvWSJY", 
  authDomain: "tttt-560ce.firebaseapp.com", 
  messagingSenderId: "87415914500"
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideClientHydration(),
    
    // ПРАВИЛЬНО: Вставляем провайдеры Firebase напрямую в массив, без importProvidersFrom
    provideFirebaseApp(() => initializeApp(firebaseConfig)),
    provideFirestore(() => getFirestore())
  ]
};
