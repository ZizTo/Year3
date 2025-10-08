import { Injectable, inject } from '@angular/core';
import { FirestoreService } from './firestore.service';
import { Observable, of } from 'rxjs';
import { Service } from '../service.model';
//import { SERVICES } from '../const-service-list';

@Injectable({
  providedIn: 'root'
})

export class StationService {
  private firestoreService = inject(FirestoreService);

  // Просто передаем "живой" поток данных из Firestore
  public services$ = this.firestoreService.getServices();

  getService(id: string): Observable<Service> {
    return this.firestoreService.getService(id);
  }

  // В Firestore ID - это строка, а наша модель Service ожидает number.
  // И Firestore сам генерирует ID. Поэтому мы убираем его перед отправкой.
  addService(service: Service): Promise<any> {
    const { id, ...data } = service;
    return this.firestoreService.addService(data);
  }

  updateService(service: Service): Promise<void> {
    return this.firestoreService.updateService(service);
  }

  deleteService(id: string): Promise<void> {
    return this.firestoreService.deleteService(id);
  }
}
/*export class StationService {
  constructor() { }

  getServices(): Observable<Service[]> {
    const services = of(SERVICES);
    return services;
  }

  getService(id: number): Observable<Service | undefined> {
    const service = SERVICES.find(s => s.id === id);
    return of(service);
  }

  updateService(serviceToUpdate: Service): Observable<Service> {
    const index = SERVICES.findIndex(s => s.id === serviceToUpdate.id);
    if (index !== -1) {
      SERVICES[index] = serviceToUpdate;
    }
    return of(serviceToUpdate);
  }

  addService(newService: Service): Observable<Service> {
    const maxId = Math.max(...SERVICES.map(s => s.id));
    newService.id = maxId + 1;
    SERVICES.push(newService);
    return of(newService);
  }

  deleteService(id: number): Observable<null> {
    const index = SERVICES.findIndex(s => s.id === id);
    if (index !== -1) {
      SERVICES.splice(index, 1);
    }
    return of(null);
  }
}*/
