import { Routes } from '@angular/router';

export const routes: Routes = [
     {
    path: '',
    redirectTo: '/services',
    pathMatch: 'full'
  },
  {
    path: 'services',
    loadChildren: () => import('./Services/services.routes').then(r => r.SERVICES_ROUTES)
  },
  {
    path: '**',
    redirectTo: '/services'
  }
];
