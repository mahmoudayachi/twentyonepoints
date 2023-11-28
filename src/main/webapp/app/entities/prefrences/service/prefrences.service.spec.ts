import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPrefrences } from '../prefrences.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../prefrences.test-samples';

import { PrefrencesService } from './prefrences.service';

const requireRestSample: IPrefrences = {
  ...sampleWithRequiredData,
};

describe('Prefrences Service', () => {
  let service: PrefrencesService;
  let httpMock: HttpTestingController;
  let expectedResult: IPrefrences | IPrefrences[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PrefrencesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Prefrences', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const prefrences = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(prefrences).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Prefrences', () => {
      const prefrences = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(prefrences).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Prefrences', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Prefrences', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Prefrences', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPrefrencesToCollectionIfMissing', () => {
      it('should add a Prefrences to an empty array', () => {
        const prefrences: IPrefrences = sampleWithRequiredData;
        expectedResult = service.addPrefrencesToCollectionIfMissing([], prefrences);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(prefrences);
      });

      it('should not add a Prefrences to an array that contains it', () => {
        const prefrences: IPrefrences = sampleWithRequiredData;
        const prefrencesCollection: IPrefrences[] = [
          {
            ...prefrences,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPrefrencesToCollectionIfMissing(prefrencesCollection, prefrences);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Prefrences to an array that doesn't contain it", () => {
        const prefrences: IPrefrences = sampleWithRequiredData;
        const prefrencesCollection: IPrefrences[] = [sampleWithPartialData];
        expectedResult = service.addPrefrencesToCollectionIfMissing(prefrencesCollection, prefrences);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(prefrences);
      });

      it('should add only unique Prefrences to an array', () => {
        const prefrencesArray: IPrefrences[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const prefrencesCollection: IPrefrences[] = [sampleWithRequiredData];
        expectedResult = service.addPrefrencesToCollectionIfMissing(prefrencesCollection, ...prefrencesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const prefrences: IPrefrences = sampleWithRequiredData;
        const prefrences2: IPrefrences = sampleWithPartialData;
        expectedResult = service.addPrefrencesToCollectionIfMissing([], prefrences, prefrences2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(prefrences);
        expect(expectedResult).toContain(prefrences2);
      });

      it('should accept null and undefined values', () => {
        const prefrences: IPrefrences = sampleWithRequiredData;
        expectedResult = service.addPrefrencesToCollectionIfMissing([], null, prefrences, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(prefrences);
      });

      it('should return initial array if no Prefrences is added', () => {
        const prefrencesCollection: IPrefrences[] = [sampleWithRequiredData];
        expectedResult = service.addPrefrencesToCollectionIfMissing(prefrencesCollection, undefined, null);
        expect(expectedResult).toEqual(prefrencesCollection);
      });
    });

    describe('comparePrefrences', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePrefrences(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePrefrences(entity1, entity2);
        const compareResult2 = service.comparePrefrences(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePrefrences(entity1, entity2);
        const compareResult2 = service.comparePrefrences(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePrefrences(entity1, entity2);
        const compareResult2 = service.comparePrefrences(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
