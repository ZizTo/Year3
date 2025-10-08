// src/app/Services/service-form/service-form.component.ts

import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { Service } from '../service.model';
import { StationService } from '../services/station.service';

@Component({
  selector: 'app-service-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './service-form.html', // Убедитесь, что расширение правильное
  styleUrl: './service-form.css'   // Убедитесь, что расширение правильное
})
export class ServiceFormComponent implements OnInit, OnDestroy {
  service: Service | undefined;
  isNew = false;
  private serviceSubscription: Subscription | undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private stationService: StationService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isNew = false;
      this.serviceSubscription = this.stationService.getService(id)
        .subscribe(data => this.service = data);
    } else {
      this.isNew = true;
      this.service = { id: '', description: '', price: 0 };
    }
  }

  ngOnDestroy(): void {
    this.serviceSubscription?.unsubscribe();
  }

  onSubmit(form: NgForm): void {
    if (form.invalid || !this.service) {
      return;
    }
    if (this.isNew) {
      this.stationService.addService(this.service).then(() => this.goBack());
    } else {
      this.stationService.updateService(this.service).then(() => this.goBack());
    }
  }

  deleteService(): void {
    if (this.service && !this.isNew && confirm(`Вы уверены, что хотите удалить "${this.service.description}"?`)) {
      this.stationService.deleteService(this.service.id).then(() => this.goBack());
    }
  }

  goBack(): void {
    this.router.navigate(['/services']);
  }
}
