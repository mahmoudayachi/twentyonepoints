import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BloodpressureComponent } from '../list/bloodpressure.component';
import { BloodpressureDetailComponent } from '../detail/bloodpressure-detail.component';
import { BloodpressureUpdateComponent } from '../update/bloodpressure-update.component';
import { BloodpressureRoutingResolveService } from './bloodpressure-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const bloodpressureRoute: Routes = [
  {
    path: '',
    component: BloodpressureComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BloodpressureDetailComponent,
    resolve: {
      bloodpressure: BloodpressureRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BloodpressureUpdateComponent,
    resolve: {
      bloodpressure: BloodpressureRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BloodpressureUpdateComponent,
    resolve: {
      bloodpressure: BloodpressureRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(bloodpressureRoute)],
  exports: [RouterModule],
})
export class BloodpressureRoutingModule {}
