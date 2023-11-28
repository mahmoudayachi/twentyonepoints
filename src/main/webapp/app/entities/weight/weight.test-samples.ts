import dayjs from 'dayjs/esm';

import { IWeight, NewWeight } from './weight.model';

export const sampleWithRequiredData: IWeight = {
  id: 86192,
  datetime: dayjs('2023-11-28T03:59'),
  weight: 42379,
};

export const sampleWithPartialData: IWeight = {
  id: 53022,
  datetime: dayjs('2023-11-28T03:22'),
  weight: 24033,
};

export const sampleWithFullData: IWeight = {
  id: 69421,
  datetime: dayjs('2023-11-28T06:19'),
  weight: 98529,
};

export const sampleWithNewData: NewWeight = {
  datetime: dayjs('2023-11-27T22:25'),
  weight: 7905,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
