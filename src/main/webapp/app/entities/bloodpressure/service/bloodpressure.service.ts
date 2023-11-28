import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IBloodpressure, NewBloodpressure } from '../bloodpressure.model';

export type PartialUpdateBloodpressure = Partial<IBloodpressure> & Pick<IBloodpressure, 'id'>;

type RestOf<T extends IBloodpressure | NewBloodpressure> = Omit<T, 'datetime'> & {
  datetime?: string | null;
};

export type RestBloodpressure = RestOf<IBloodpressure>;

export type NewRestBloodpressure = RestOf<NewBloodpressure>;

export type PartialUpdateRestBloodpressure = RestOf<PartialUpdateBloodpressure>;

export type EntityResponseType = HttpResponse<IBloodpressure>;
export type EntityArrayResponseType = HttpResponse<IBloodpressure[]>;

@Injectable({ providedIn: 'root' })
export class BloodpressureService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/bloodpressures');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/bloodpressures');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(bloodpressure: NewBloodpressure): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bloodpressure);
    return this.http
      .post<RestBloodpressure>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(bloodpressure: IBloodpressure): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bloodpressure);
    return this.http
      .put<RestBloodpressure>(`${this.resourceUrl}/${this.getBloodpressureIdentifier(bloodpressure)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(bloodpressure: PartialUpdateBloodpressure): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bloodpressure);
    return this.http
      .patch<RestBloodpressure>(`${this.resourceUrl}/${this.getBloodpressureIdentifier(bloodpressure)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestBloodpressure>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBloodpressure[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBloodpressure[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getBloodpressureIdentifier(bloodpressure: Pick<IBloodpressure, 'id'>): number {
    return bloodpressure.id;
  }

  compareBloodpressure(o1: Pick<IBloodpressure, 'id'> | null, o2: Pick<IBloodpressure, 'id'> | null): boolean {
    return o1 && o2 ? this.getBloodpressureIdentifier(o1) === this.getBloodpressureIdentifier(o2) : o1 === o2;
  }

  addBloodpressureToCollectionIfMissing<Type extends Pick<IBloodpressure, 'id'>>(
    bloodpressureCollection: Type[],
    ...bloodpressuresToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bloodpressures: Type[] = bloodpressuresToCheck.filter(isPresent);
    if (bloodpressures.length > 0) {
      const bloodpressureCollectionIdentifiers = bloodpressureCollection.map(
        bloodpressureItem => this.getBloodpressureIdentifier(bloodpressureItem)!
      );
      const bloodpressuresToAdd = bloodpressures.filter(bloodpressureItem => {
        const bloodpressureIdentifier = this.getBloodpressureIdentifier(bloodpressureItem);
        if (bloodpressureCollectionIdentifiers.includes(bloodpressureIdentifier)) {
          return false;
        }
        bloodpressureCollectionIdentifiers.push(bloodpressureIdentifier);
        return true;
      });
      return [...bloodpressuresToAdd, ...bloodpressureCollection];
    }
    return bloodpressureCollection;
  }

  protected convertDateFromClient<T extends IBloodpressure | NewBloodpressure | PartialUpdateBloodpressure>(bloodpressure: T): RestOf<T> {
    return {
      ...bloodpressure,
      datetime: bloodpressure.datetime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restBloodpressure: RestBloodpressure): IBloodpressure {
    return {
      ...restBloodpressure,
      datetime: restBloodpressure.datetime ? dayjs(restBloodpressure.datetime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestBloodpressure>): HttpResponse<IBloodpressure> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestBloodpressure[]>): HttpResponse<IBloodpressure[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
