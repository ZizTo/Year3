import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceCenter } from './service-center';

describe('ServiceCenter', () => {
  let component: ServiceCenter;
  let fixture: ComponentFixture<ServiceCenter>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServiceCenter]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServiceCenter);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
