import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBloodpressure, NewBloodpressure } from '../bloodpressure.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBloodpressure for edit and NewBloodpressureFormGroupInput for create.
 */
type BloodpressureFormGroupInput = IBloodpressure | PartialWithRequiredKeyOf<NewBloodpressure>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBloodpressure | NewBloodpressure> = Omit<T, 'datetime'> & {
  datetime?: string | null;
};

type BloodpressureFormRawValue = FormValueOf<IBloodpressure>;

type NewBloodpressureFormRawValue = FormValueOf<NewBloodpressure>;

type BloodpressureFormDefaults = Pick<NewBloodpressure, 'id' | 'datetime'>;

type BloodpressureFormGroupContent = {
  id: FormControl<BloodpressureFormRawValue['id'] | NewBloodpressure['id']>;
  datetime: FormControl<BloodpressureFormRawValue['datetime']>;
  systolic: FormControl<BloodpressureFormRawValue['systolic']>;
  diastolic: FormControl<BloodpressureFormRawValue['diastolic']>;
  userbloodpressure: FormControl<BloodpressureFormRawValue['userbloodpressure']>;
};

export type BloodpressureFormGroup = FormGroup<BloodpressureFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BloodpressureFormService {
  createBloodpressureFormGroup(bloodpressure: BloodpressureFormGroupInput = { id: null }): BloodpressureFormGroup {
    const bloodpressureRawValue = this.convertBloodpressureToBloodpressureRawValue({
      ...this.getFormDefaults(),
      ...bloodpressure,
    });
    return new FormGroup<BloodpressureFormGroupContent>({
      id: new FormControl(
        { value: bloodpressureRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      datetime: new FormControl(bloodpressureRawValue.datetime, {
        validators: [Validators.required],
      }),
      systolic: new FormControl(bloodpressureRawValue.systolic, {
        validators: [Validators.required],
      }),
      diastolic: new FormControl(bloodpressureRawValue.diastolic, {
        validators: [Validators.required],
      }),
      userbloodpressure: new FormControl(bloodpressureRawValue.userbloodpressure),
    });
  }

  getBloodpressure(form: BloodpressureFormGroup): IBloodpressure | NewBloodpressure {
    return this.convertBloodpressureRawValueToBloodpressure(form.getRawValue() as BloodpressureFormRawValue | NewBloodpressureFormRawValue);
  }

  resetForm(form: BloodpressureFormGroup, bloodpressure: BloodpressureFormGroupInput): void {
    const bloodpressureRawValue = this.convertBloodpressureToBloodpressureRawValue({ ...this.getFormDefaults(), ...bloodpressure });
    form.reset(
      {
        ...bloodpressureRawValue,
        id: { value: bloodpressureRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BloodpressureFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      datetime: currentTime,
    };
  }

  private convertBloodpressureRawValueToBloodpressure(
    rawBloodpressure: BloodpressureFormRawValue | NewBloodpressureFormRawValue
  ): IBloodpressure | NewBloodpressure {
    return {
      ...rawBloodpressure,
      datetime: dayjs(rawBloodpressure.datetime, DATE_TIME_FORMAT),
    };
  }

  private convertBloodpressureToBloodpressureRawValue(
    bloodpressure: IBloodpressure | (Partial<NewBloodpressure> & BloodpressureFormDefaults)
  ): BloodpressureFormRawValue | PartialWithRequiredKeyOf<NewBloodpressureFormRawValue> {
    return {
      ...bloodpressure,
      datetime: bloodpressure.datetime ? bloodpressure.datetime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
