import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { Subscriber } from '../../../models/subscriber.model';

@Component({
  selector: 'app-subscriber-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './subscriber-list.component.html'
})
export class SubscriberListComponent implements OnInit {

  subscribers: Subscriber[] = [];
  loading = false;

  constructor(public router: Router, private apiService: ApiService) { }

  ngOnInit(): void {
    this.loadSubscribers();
  }

  loadSubscribers(): void {
    this.loading = true;
    this.apiService.getSubscribers().subscribe(
      (data) => {
        this.subscribers = data;
        this.loading = false;
      },
      (error) => {
        console.error('Ошибка загрузки абонентов:', error);
        this.loading = false;
      }
    );
  }

  deleteSubscriber(id: number | undefined): void {
    if (id && confirm('Удалить абонента?')) {
      this.apiService.deleteSubscriber(id).subscribe(
        () => {
          this.subscribers = this.subscribers.filter(s => s.id !== id);
        },
        (error) => console.error('Ошибка удаления:', error)
      );
    }
  }

  editSubscriber(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/subscribers', id, 'edit']);
    }
  }

  viewSubscriber(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/subscribers', id]);
    }
  }
}
