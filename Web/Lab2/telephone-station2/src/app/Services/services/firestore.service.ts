// src/app/Services/services/firestore.service.ts

import { Injectable, inject } from '@angular/core';
import { Firestore, collection, collectionData, doc, docData, addDoc, updateDoc, deleteDoc } from '@angular/fire/firestore';
import { Service } from '../service.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FirestoreService {
  private firestore: Firestore = inject(Firestore);

  // Получить ВСЕ услуги (возвращает "живой" Observable, который сам обновляется)
  getServices(): Observable<Service[]> {
    const servicesCollection = collection(this.firestore, 'services');
    return collectionData(servicesCollection, { idField: 'id' }) as Observable<Service[]>;
  }

  // Получить ОДНУ услугу по ID
  getService(id: string): Observable<Service> {
    const serviceDoc = doc(this.firestore, `services/${id}`);
    return docData(serviceDoc, { idField: 'id' }) as Observable<Service>;
  }

  // ДОБАВИТЬ новую услугу
  addService(service: Omit<Service, 'id'>) {
    const servicesCollection = collection(this.firestore, 'services');
    return addDoc(servicesCollection, service);
  }

  // ОБНОВИТЬ существующую услугу
  updateService(service: Service) {
    const serviceDoc = doc(this.firestore, `services/${service.id}`);
    // Omit 'id' because we don't want to save the id field inside the document
    const { id, ...data } = service;
    return updateDoc(serviceDoc, data);
  }

  // УДАЛИТЬ услугу
  deleteService(id: string) {
    const serviceDoc = doc(this.firestore, `services/${id}`);
    return deleteDoc(serviceDoc);
  }
}
