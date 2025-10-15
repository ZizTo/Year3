import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Observable, of, switchMap } from 'rxjs';

import { Service } from '../service.model';
import { StationService } from '../services/station.service';

@Component({
  selector: 'app-service-details',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './service-details.html',
  styleUrl: './service-details.css'
})
export class ServiceDetailsComponent implements OnInit {
  
  public service$!: Observable<Service | undefined>;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private stationService: StationService
  ) { }

  ngOnInit(): void {
    this.service$ = this.route.paramMap.pipe(
      switchMap(params => {
        const id = params.get('id');
        if (id) {
          return this.stationService.getService(id);
        }
        return of(undefined);
      })
    );
  }

  gotoServices() {
    this.router.navigate(['/services']);
  }
}
