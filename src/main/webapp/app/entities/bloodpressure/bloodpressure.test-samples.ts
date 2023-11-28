import dayjs from 'dayjs/esm';

import { IBloodpressure, NewBloodpressure } from './bloodpressure.model';

export const sampleWithRequiredData: IBloodpressure = {
  id: 70500,
  datetime: dayjs('2023-11-28T19:30'),
  systolic: 28382,
  diastolic: 8618,
};

export const sampleWithPartialData: IBloodpressure = {
  id: 66587,
  datetime: dayjs('2023-11-28T06:55'),
  systolic: 93053,
  diastolic: 73337,
};

export const sampleWithFullData: IBloodpressure = {
  id: 62779,
  datetime: dayjs('2023-11-28T05:04'),
  systolic: 98767,
  diastolic: 59202,
};

export const sampleWithNewData: NewBloodpressure = {
  datetime: dayjs('2023-11-28T16:18'),
  systolic: 49029,
  diastolic: 4940,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
