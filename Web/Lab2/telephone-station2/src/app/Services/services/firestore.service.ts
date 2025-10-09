import { Injectable, inject } from '@angular/core';
import { Firestore, collection, collectionData, doc, docData, addDoc, updateDoc, deleteDoc } from '@angular/fire/firestore';
import { Service } from '../service.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FirestoreService {
  private firestore: Firestore = inject(Firestore);

  getServices(): Observable<Service[]> {
    const servicesCollection = collection(this.firestore, 'services');
    return collectionData(servicesCollection, { idField: 'id' }) as Observable<Service[]>;
  }

  getService(id: string): Observable<Service> {
    const serviceDoc = doc(this.firestore, `services/${id}`);
    return docData(serviceDoc, { idField: 'id' }) as Observable<Service>;
  }

  addService(service: Omit<Service, 'id'>) {
    const servicesCollection = collection(this.firestore, 'services');
    return addDoc(servicesCollection, service);
  }

  updateService(service: Service) {
    const serviceDoc = doc(this.firestore, `services/${service.id}`);
    const { id, ...data } = service;
    return updateDoc(serviceDoc, data);
  }

  deleteService(id: string) {
    const serviceDoc = doc(this.firestore, `services/${id}`);
    return deleteDoc(serviceDoc);
  }
}
