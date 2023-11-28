import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPrefrences } from '../prefrences.model';
import { PrefrencesService } from '../service/prefrences.service';

@Injectable({ providedIn: 'root' })
export class PrefrencesRoutingResolveService implements Resolve<IPrefrences | null> {
  constructor(protected service: PrefrencesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPrefrences | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((prefrences: HttpResponse<IPrefrences>) => {
          if (prefrences.body) {
            return of(prefrences.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
