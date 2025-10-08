// src/app/Services/services.routes.ts

import { Routes } from '@angular/router';

import { ServiceCenterComponent } from './service-center/service-center.component'
import { ServiceDetailsComponent } from './service-details/service-details.component'
import { ServiceListComponent } from './service-list/service-list.component'
import { ServiceFormComponent } from './service-form/service-form.component'

/*export const SERVICES_ROUTES: Routes = [
  {
    path: '',
    component: ServiceCenterComponent,
    children: [
      {
        path: '',
        component: ServiceListComponent,
        children: [
          { path: 'new', component: ServiceFormComponent },
          {
            path: ':id',
            component: ServiceDetailsComponent
          },
          { path: ':id/edit', component: ServiceFormComponent },
        ]
      }
    ]
  }
];*/

export const SERVICES_ROUTES: Routes = [
  { path: '', component: ServiceListComponent },
  { path: 'new', component: ServiceFormComponent },
  { path: ':id', component: ServiceDetailsComponent },
  { path: ':id/edit', component: ServiceFormComponent },
];
