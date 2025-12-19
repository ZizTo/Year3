import { Routes } from '@angular/router';
import { SubscriberListComponent } from './components/subscribers/subscriber-list/subscriber-list.component';
import { SubscriberFormComponent } from './components/subscribers/subscriber-form/subscriber-form.component';
import { ServiceListComponent } from './components/services/service-list/service-list.component';
import { ServiceFormComponent } from './components/services/service-form/service-form.component';
import { BillListComponent } from './components/bills/bill-list/bill-list.component';
import { BillFormComponent } from './components/bills/bill-form/bill-form.component';

export const routes: Routes = [
  {
    path: 'subscribers',
    children: [
      { path: '', component: SubscriberListComponent },
      { path: 'new', component: SubscriberFormComponent },
      { path: ':id/edit', component: SubscriberFormComponent }
    ]
  },
  {
    path: 'services',
    children: [
      { path: '', component: ServiceListComponent },
      { path: 'new', component: ServiceFormComponent },
      { path: ':id/edit', component: ServiceFormComponent }
    ]
  },
  {
    path: 'bills',
    children: [
      { path: '', component: BillListComponent },
      { path: 'new', component: BillFormComponent },
      { path: ':id/edit', component: BillFormComponent }
    ]
  },
  { path: '', redirectTo: '/subscribers', pathMatch: 'full' },
  { path: '**', redirectTo: '/subscribers' }
];
