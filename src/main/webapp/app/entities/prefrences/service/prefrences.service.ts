import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPrefrences, NewPrefrences } from '../prefrences.model';

export type PartialUpdatePrefrences = Partial<IPrefrences> & Pick<IPrefrences, 'id'>;

export type EntityResponseType = HttpResponse<IPrefrences>;
export type EntityArrayResponseType = HttpResponse<IPrefrences[]>;

@Injectable({ providedIn: 'root' })
export class PrefrencesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/prefrences');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/prefrences');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(prefrences: NewPrefrences): Observable<EntityResponseType> {
    return this.http.post<IPrefrences>(this.resourceUrl, prefrences, { observe: 'response' });
  }

  update(prefrences: IPrefrences): Observable<EntityResponseType> {
    return this.http.put<IPrefrences>(`${this.resourceUrl}/${this.getPrefrencesIdentifier(prefrences)}`, prefrences, {
      observe: 'response',
    });
  }

  partialUpdate(prefrences: PartialUpdatePrefrences): Observable<EntityResponseType> {
    return this.http.patch<IPrefrences>(`${this.resourceUrl}/${this.getPrefrencesIdentifier(prefrences)}`, prefrences, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPrefrences>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPrefrences[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPrefrences[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getPrefrencesIdentifier(prefrences: Pick<IPrefrences, 'id'>): number {
    return prefrences.id;
  }

  comparePrefrences(o1: Pick<IPrefrences, 'id'> | null, o2: Pick<IPrefrences, 'id'> | null): boolean {
    return o1 && o2 ? this.getPrefrencesIdentifier(o1) === this.getPrefrencesIdentifier(o2) : o1 === o2;
  }

  addPrefrencesToCollectionIfMissing<Type extends Pick<IPrefrences, 'id'>>(
    prefrencesCollection: Type[],
    ...prefrencesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const prefrences: Type[] = prefrencesToCheck.filter(isPresent);
    if (prefrences.length > 0) {
      const prefrencesCollectionIdentifiers = prefrencesCollection.map(prefrencesItem => this.getPrefrencesIdentifier(prefrencesItem)!);
      const prefrencesToAdd = prefrences.filter(prefrencesItem => {
        const prefrencesIdentifier = this.getPrefrencesIdentifier(prefrencesItem);
        if (prefrencesCollectionIdentifiers.includes(prefrencesIdentifier)) {
          return false;
        }
        prefrencesCollectionIdentifiers.push(prefrencesIdentifier);
        return true;
      });
      return [...prefrencesToAdd, ...prefrencesCollection];
    }
    return prefrencesCollection;
  }
}
