import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { Subscriber } from '../../../models/subscriber.model';

@Component({
  selector: 'app-subscriber-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './subscriber-form.component.html'
})
export class SubscriberFormComponent implements OnInit {

  subscriber: Subscriber = {
    fullName: '',
    phoneNumber: '',
    blocked: false
  };
  isEdit = false;
  loading = false;

  constructor(
    private apiService: ApiService,
    private route: ActivatedRoute,
    public router: Router
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.loadSubscriber(+id);
    }
  }

  loadSubscriber(id: number): void {
    this.loading = true;
    this.apiService.getSubscriber(id).subscribe(
      (data) => {
        this.subscriber = data;
        this.loading = false;
      },
      (error) => {
        console.error('Ошибка загрузки:', error);
        this.loading = false;
      }
    );
  }

  saveSubscriber(): void {
    if (this.isEdit && this.subscriber.id) {
      this.apiService.updateSubscriber(this.subscriber.id, this.subscriber).subscribe(
        () => {
          this.router.navigate(['/subscribers']);
        },
        (error) => console.error('Ошибка обновления:', error)
      );
    } else {
      this.apiService.createSubscriber(this.subscriber).subscribe(
        () => {
          this.router.navigate(['/subscribers']);
        },
        (error) => console.error('Ошибка создания:', error)
      );
    }
  }

  cancel(): void {
    this.router.navigate(['/subscribers']);
  }
}
