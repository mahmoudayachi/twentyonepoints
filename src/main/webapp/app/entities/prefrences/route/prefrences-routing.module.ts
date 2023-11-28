import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PrefrencesComponent } from '../list/prefrences.component';
import { PrefrencesDetailComponent } from '../detail/prefrences-detail.component';
import { PrefrencesUpdateComponent } from '../update/prefrences-update.component';
import { PrefrencesRoutingResolveService } from './prefrences-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const prefrencesRoute: Routes = [
  {
    path: '',
    component: PrefrencesComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PrefrencesDetailComponent,
    resolve: {
      prefrences: PrefrencesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PrefrencesUpdateComponent,
    resolve: {
      prefrences: PrefrencesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PrefrencesUpdateComponent,
    resolve: {
      prefrences: PrefrencesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(prefrencesRoute)],
  exports: [RouterModule],
})
export class PrefrencesRoutingModule {}
