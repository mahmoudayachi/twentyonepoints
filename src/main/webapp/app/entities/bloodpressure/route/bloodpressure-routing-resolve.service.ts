import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBloodpressure } from '../bloodpressure.model';
import { BloodpressureService } from '../service/bloodpressure.service';

@Injectable({ providedIn: 'root' })
export class BloodpressureRoutingResolveService implements Resolve<IBloodpressure | null> {
  constructor(protected service: BloodpressureService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBloodpressure | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((bloodpressure: HttpResponse<IBloodpressure>) => {
          if (bloodpressure.body) {
            return of(bloodpressure.body);
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
