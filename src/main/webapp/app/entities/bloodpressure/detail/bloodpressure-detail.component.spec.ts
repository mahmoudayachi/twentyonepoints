import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BloodpressureDetailComponent } from './bloodpressure-detail.component';

describe('Bloodpressure Management Detail Component', () => {
  let comp: BloodpressureDetailComponent;
  let fixture: ComponentFixture<BloodpressureDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BloodpressureDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ bloodpressure: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(BloodpressureDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BloodpressureDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load bloodpressure on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.bloodpressure).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
