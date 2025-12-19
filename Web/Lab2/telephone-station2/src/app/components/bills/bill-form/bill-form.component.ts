import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { Bill, Subscriber } from '../../../models/subscriber.model';

@Component({
  selector: 'app-bill-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bill-form.component.html'
})
export class BillFormComponent implements OnInit {

  bill: Bill = {
    amount: 0,
    issueDate: new Date().toISOString().split('T')[0],
    paid: false
  };
  subscribers: Subscriber[] = [];
  isEdit = false;
  loading = false;

  constructor(
    private apiService: ApiService,
    private route: ActivatedRoute,
    public router: Router
  ) { }

  ngOnInit(): void {
    this.loadSubscribers();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.loadBill(+id);
    }
  }

  loadSubscribers(): void {
    this.apiService.getSubscribers().subscribe(
      (data) => {
        this.subscribers = data;
      },
      (error) => console.error('Ошибка загрузки абонентов:', error)
    );
  }

  loadBill(id: number): void {
    this.loading = true;
    this.apiService.getBill(id).subscribe(
      (data) => {
        this.bill = data;
        this.loading = false;
      },
      (error) => {
        console.error('Ошибка загрузки:', error);
        this.loading = false;
      }
    );
  }

  saveBill(): void {
    if (this.isEdit && this.bill.id) {
      this.apiService.updateBill(this.bill.id, this.bill).subscribe(
        () => {
          this.router.navigate(['/bills']);
        },
        (error) => console.error('Ошибка обновления:', error)
      );
    } else {
      this.apiService.createBill(this.bill).subscribe(
        () => {
          this.router.navigate(['/bills']);
        },
        (error) => console.error('Ошибка создания:', error)
      );
    }
  }

  cancel(): void {
    this.router.navigate(['/bills']);
  }
}
