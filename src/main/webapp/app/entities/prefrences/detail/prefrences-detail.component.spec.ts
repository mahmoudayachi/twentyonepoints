import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PrefrencesDetailComponent } from './prefrences-detail.component';

describe('Prefrences Management Detail Component', () => {
  let comp: PrefrencesDetailComponent;
  let fixture: ComponentFixture<PrefrencesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PrefrencesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ prefrences: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PrefrencesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PrefrencesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load prefrences on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.prefrences).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
