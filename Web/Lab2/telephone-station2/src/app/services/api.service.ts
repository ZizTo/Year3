import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Subscriber, Service, Bill } from '../models/subscriber.model';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private apiUrl = 'http://localhost:8082/DemoSpring/api';

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private httpClient: HttpClient) { }


  getSubscribers(): Observable<Subscriber[]> {
    return this.httpClient.get<Subscriber[]>(`${this.apiUrl}/subscribers`);
  }

  getSubscriber(id: number): Observable<Subscriber> {
    return this.httpClient.get<Subscriber>(`${this.apiUrl}/subscribers/${id}`);
  }

  createSubscriber(subscriber: Subscriber): Observable<Subscriber> {
    return this.httpClient.post<Subscriber>(`${this.apiUrl}/subscribers`, subscriber, this.httpOptions);
  }

  updateSubscriber(id: number, subscriber: Subscriber): Observable<Subscriber> {
    return this.httpClient.put<Subscriber>(`${this.apiUrl}/subscribers/${id}`, subscriber, this.httpOptions);
  }

  deleteSubscriber(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/subscribers/${id}`);
  }



  getServices(): Observable<Service[]> {
    return this.httpClient.get<Service[]>(`${this.apiUrl}/services`);
  }

  getService(id: number): Observable<Service> {
    return this.httpClient.get<Service>(`${this.apiUrl}/services/${id}`);
  }

  createService(service: Service): Observable<Service> {
    return this.httpClient.post<Service>(`${this.apiUrl}/services`, service, this.httpOptions);
  }

  updateService(id: number, service: Service): Observable<Service> {
    return this.httpClient.put<Service>(`${this.apiUrl}/services/${id}`, service, this.httpOptions);
  }

  deleteService(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/services/${id}`);
  }



  getBills(): Observable<Bill[]> {
    return this.httpClient.get<Bill[]>(`${this.apiUrl}/bills`);
  }

  getBill(id: number): Observable<Bill> {
    return this.httpClient.get<Bill>(`${this.apiUrl}/bills/${id}`);
  }

  createBill(bill: Bill): Observable<Bill> {
    return this.httpClient.post<Bill>(`${this.apiUrl}/bills`, bill, this.httpOptions);
  }

  updateBill(id: number, bill: Bill): Observable<Bill> {
    return this.httpClient.put<Bill>(`${this.apiUrl}/bills/${id}`, bill, this.httpOptions);
  }

  deleteBill(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/bills/${id}`);
  }

  payBill(id: number): Observable<Bill> {
    return this.httpClient.put<Bill>(`${this.apiUrl}/bills/${id}/pay`, {}, this.httpOptions);
  }
}
