import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { Service } from '../../../models/subscriber.model';

@Component({
  selector: 'app-service-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './service-list.component.html'
})
export class ServiceListComponent implements OnInit {

  services: Service[] = [];
  loading = false;

  constructor(public router: Router, private apiService: ApiService) { }

  ngOnInit(): void {
    this.loadServices();
  }

  loadServices(): void {
    this.loading = true;
    this.apiService.getServices().subscribe(
      (data) => {
        this.services = data;
        this.loading = false;
      },
      (error) => {
        console.error('Ошибка загрузки услуг:', error);
        this.loading = false;
      }
    );
  }

  deleteService(id: number | undefined): void {
    if (id && confirm('Удалить услугу?')) {
      this.apiService.deleteService(id).subscribe(
        () => {
          this.services = this.services.filter(s => s.id !== id);
        },
        (error) => console.error('Ошибка удаления:', error)
      );
    }
  }

  editService(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/services', id, 'edit']);
    }
  }
}
