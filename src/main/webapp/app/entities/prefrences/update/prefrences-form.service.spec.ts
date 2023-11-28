import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../prefrences.test-samples';

import { PrefrencesFormService } from './prefrences-form.service';

describe('Prefrences Form Service', () => {
  let service: PrefrencesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PrefrencesFormService);
  });

  describe('Service methods', () => {
    describe('createPrefrencesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPrefrencesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            weeklygoal: expect.any(Object),
            weightunits: expect.any(Object),
            userprefrences: expect.any(Object),
          })
        );
      });

      it('passing IPrefrences should create a new form with FormGroup', () => {
        const formGroup = service.createPrefrencesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            weeklygoal: expect.any(Object),
            weightunits: expect.any(Object),
            userprefrences: expect.any(Object),
          })
        );
      });
    });

    describe('getPrefrences', () => {
      it('should return NewPrefrences for default Prefrences initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPrefrencesFormGroup(sampleWithNewData);

        const prefrences = service.getPrefrences(formGroup) as any;

        expect(prefrences).toMatchObject(sampleWithNewData);
      });

      it('should return NewPrefrences for empty Prefrences initial value', () => {
        const formGroup = service.createPrefrencesFormGroup();

        const prefrences = service.getPrefrences(formGroup) as any;

        expect(prefrences).toMatchObject({});
      });

      it('should return IPrefrences', () => {
        const formGroup = service.createPrefrencesFormGroup(sampleWithRequiredData);

        const prefrences = service.getPrefrences(formGroup) as any;

        expect(prefrences).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPrefrences should not enable id FormControl', () => {
        const formGroup = service.createPrefrencesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPrefrences should disable id FormControl', () => {
        const formGroup = service.createPrefrencesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
