import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Observable } from 'rxjs';

import { Service } from '../service.model';
import { StationService } from '../services/station.service';

@Component({
  selector: 'app-service-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './service-list.html',
  styleUrl: './service-list.css'
})
export class ServiceListComponent implements OnInit {
  public services$!: Observable<Service[]>;

  constructor(private stationService: StationService) { }

  ngOnInit(): void {
    this.services$ = this.stationService.services$;
  }
}
