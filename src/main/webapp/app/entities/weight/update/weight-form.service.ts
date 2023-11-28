import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IWeight, NewWeight } from '../weight.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWeight for edit and NewWeightFormGroupInput for create.
 */
type WeightFormGroupInput = IWeight | PartialWithRequiredKeyOf<NewWeight>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IWeight | NewWeight> = Omit<T, 'datetime'> & {
  datetime?: string | null;
};

type WeightFormRawValue = FormValueOf<IWeight>;

type NewWeightFormRawValue = FormValueOf<NewWeight>;

type WeightFormDefaults = Pick<NewWeight, 'id' | 'datetime'>;

type WeightFormGroupContent = {
  id: FormControl<WeightFormRawValue['id'] | NewWeight['id']>;
  datetime: FormControl<WeightFormRawValue['datetime']>;
  weight: FormControl<WeightFormRawValue['weight']>;
  userweight: FormControl<WeightFormRawValue['userweight']>;
};

export type WeightFormGroup = FormGroup<WeightFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class WeightFormService {
  createWeightFormGroup(weight: WeightFormGroupInput = { id: null }): WeightFormGroup {
    const weightRawValue = this.convertWeightToWeightRawValue({
      ...this.getFormDefaults(),
      ...weight,
    });
    return new FormGroup<WeightFormGroupContent>({
      id: new FormControl(
        { value: weightRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      datetime: new FormControl(weightRawValue.datetime, {
        validators: [Validators.required],
      }),
      weight: new FormControl(weightRawValue.weight, {
        validators: [Validators.required],
      }),
      userweight: new FormControl(weightRawValue.userweight),
    });
  }

  getWeight(form: WeightFormGroup): IWeight | NewWeight {
    return this.convertWeightRawValueToWeight(form.getRawValue() as WeightFormRawValue | NewWeightFormRawValue);
  }

  resetForm(form: WeightFormGroup, weight: WeightFormGroupInput): void {
    const weightRawValue = this.convertWeightToWeightRawValue({ ...this.getFormDefaults(), ...weight });
    form.reset(
      {
        ...weightRawValue,
        id: { value: weightRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): WeightFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      datetime: currentTime,
    };
  }

  private convertWeightRawValueToWeight(rawWeight: WeightFormRawValue | NewWeightFormRawValue): IWeight | NewWeight {
    return {
      ...rawWeight,
      datetime: dayjs(rawWeight.datetime, DATE_TIME_FORMAT),
    };
  }

  private convertWeightToWeightRawValue(
    weight: IWeight | (Partial<NewWeight> & WeightFormDefaults)
  ): WeightFormRawValue | PartialWithRequiredKeyOf<NewWeightFormRawValue> {
    return {
      ...weight,
      datetime: weight.datetime ? weight.datetime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
