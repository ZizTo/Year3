// src/app/Services/service-list/service-list.component.ts

import { Component, OnInit } from '@angular/core'; // 1. Возвращаем OnInit
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Observable } from 'rxjs';

import { Service } from '../service.model';
import { StationService } from '../services/station.service';

@Component({
  selector: 'app-service-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './service-list.html', // Убедитесь, что расширение правильное
  styleUrl: './service-list.css'   // Убедитесь, что расширение правильное
})
export class ServiceListComponent implements OnInit { // 2. Реализуем OnInit
  
  // 3. Объявляем свойство, но не инициализируем
  public services$!: Observable<Service[]>;

  // 4. Конструктор ТОЛЬКО для инъекций. Тело пустое.
  constructor(private stationService: StationService) { }

  // 5. ВСЯ логика переезжает в ngOnInit
  ngOnInit(): void {
    this.services$ = this.stationService.services$;
  }
}
