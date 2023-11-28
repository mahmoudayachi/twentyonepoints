import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPrefrences, NewPrefrences } from '../prefrences.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPrefrences for edit and NewPrefrencesFormGroupInput for create.
 */
type PrefrencesFormGroupInput = IPrefrences | PartialWithRequiredKeyOf<NewPrefrences>;

type PrefrencesFormDefaults = Pick<NewPrefrences, 'id'>;

type PrefrencesFormGroupContent = {
  id: FormControl<IPrefrences['id'] | NewPrefrences['id']>;
  weeklygoal: FormControl<IPrefrences['weeklygoal']>;
  weightunits: FormControl<IPrefrences['weightunits']>;
  userprefrences: FormControl<IPrefrences['userprefrences']>;
};

export type PrefrencesFormGroup = FormGroup<PrefrencesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PrefrencesFormService {
  createPrefrencesFormGroup(prefrences: PrefrencesFormGroupInput = { id: null }): PrefrencesFormGroup {
    const prefrencesRawValue = {
      ...this.getFormDefaults(),
      ...prefrences,
    };
    return new FormGroup<PrefrencesFormGroupContent>({
      id: new FormControl(
        { value: prefrencesRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      weeklygoal: new FormControl(prefrencesRawValue.weeklygoal, {
        validators: [Validators.required, Validators.min(10), Validators.max(21)],
      }),
      weightunits: new FormControl(prefrencesRawValue.weightunits, {
        validators: [Validators.required],
      }),
      userprefrences: new FormControl(prefrencesRawValue.userprefrences),
    });
  }

  getPrefrences(form: PrefrencesFormGroup): IPrefrences | NewPrefrences {
    return form.getRawValue() as IPrefrences | NewPrefrences;
  }

  resetForm(form: PrefrencesFormGroup, prefrences: PrefrencesFormGroupInput): void {
    const prefrencesRawValue = { ...this.getFormDefaults(), ...prefrences };
    form.reset(
      {
        ...prefrencesRawValue,
        id: { value: prefrencesRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PrefrencesFormDefaults {
    return {
      id: null,
    };
  }
}
