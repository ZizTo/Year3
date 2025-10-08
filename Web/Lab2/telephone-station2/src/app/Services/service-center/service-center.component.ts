// src/app/Services/service-center/service-center.component.ts

import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-service-center',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './service-center.html', // Убедитесь, что расширение правильное
  styleUrl: './service-center.css'   // Убедитесь, что расширение правильное
})
export class ServiceCenterComponent {
  // Конструктор пустой, логики нет. Это самый безопасный вариант.
  constructor() { }
}
