import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { Bill } from '../../../models/subscriber.model';

@Component({
  selector: 'app-bill-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './bill-list.component.html'
})
export class BillListComponent implements OnInit {

  bills: Bill[] = [];
  loading = false;

  constructor(public router: Router, private apiService: ApiService) { }

  ngOnInit(): void {
    this.loadBills();
  }

  loadBills(): void {
    this.loading = true;
    this.apiService.getBills().subscribe(
      (data) => {
        this.bills = data;
        this.loading = false;
      },
      (error) => {
        console.error('Ошибка загрузки счетов:', error);
        this.loading = false;
      }
    );
  }

  payBill(id: number | undefined): void {
    if (id && confirm('Оплатить счет?')) {
      this.apiService.payBill(id).subscribe(
        (updatedBill) => {
          const index = this.bills.findIndex(b => b.id === id);
          if (index !== -1) {
            this.bills[index] = updatedBill;
          }
        },
        (error) => console.error('Ошибка оплаты:', error)
      );
    }
  }

  deleteBill(id: number | undefined): void {
    if (id && confirm('Удалить счет?')) {
      this.apiService.deleteBill(id).subscribe(
        () => {
          this.bills = this.bills.filter(b => b.id !== id);
        },
        (error) => console.error('Ошибка удаления:', error)
      );
    }
  }

  editBill(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/bills', id, 'edit']);
    }
  }
}
