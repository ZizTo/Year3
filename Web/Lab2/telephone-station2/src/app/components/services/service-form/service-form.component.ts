import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { Service } from '../../../models/subscriber.model';

@Component({
  selector: 'app-service-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './service-form.component.html'
})
export class ServiceFormComponent implements OnInit {

  service: Service = {
    name: '',
    monthlyCost: 0
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
      this.loadService(+id);
    }
  }

  loadService(id: number): void {
    this.loading = true;
    this.apiService.getService(id).subscribe(
      (data) => {
        this.service = data;
        this.loading = false;
      },
      (error) => {
        console.error('Ошибка загрузки:', error);
        this.loading = false;
      }
    );
  }

  saveService(): void {
    if (this.isEdit && this.service.id) {
      this.apiService.updateService(this.service.id, this.service).subscribe(
        () => {
          this.router.navigate(['/services']);
        },
        (error) => console.error('Ошибка обновления:', error)
      );
    } else {
      this.apiService.createService(this.service).subscribe(
        () => {
          this.router.navigate(['/services']);
        },
        (error) => console.error('Ошибка создания:', error)
      );
    }
  }

  cancel(): void {
    this.router.navigate(['/services']);
  }
}
