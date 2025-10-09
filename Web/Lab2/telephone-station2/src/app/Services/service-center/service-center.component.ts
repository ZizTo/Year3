import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-service-center',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './service-center.html',
  styleUrl: './service-center.css'
})
export class ServiceCenterComponent {
  constructor() { }
}
