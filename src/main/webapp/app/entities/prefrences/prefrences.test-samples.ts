import { units } from 'app/entities/enumerations/units.model';

import { IPrefrences, NewPrefrences } from './prefrences.model';

export const sampleWithRequiredData: IPrefrences = {
  id: 15840,
  weeklygoal: 18,
  weightunits: units['KG'],
};

export const sampleWithPartialData: IPrefrences = {
  id: 59999,
  weeklygoal: 16,
  weightunits: units['LB'],
};

export const sampleWithFullData: IPrefrences = {
  id: 87812,
  weeklygoal: 19,
  weightunits: units['LB'],
};

export const sampleWithNewData: NewPrefrences = {
  weeklygoal: 20,
  weightunits: units['LB'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
