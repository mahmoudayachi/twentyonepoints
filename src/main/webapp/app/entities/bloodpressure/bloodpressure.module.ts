import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BloodpressureComponent } from './list/bloodpressure.component';
import { BloodpressureDetailComponent } from './detail/bloodpressure-detail.component';
import { BloodpressureUpdateComponent } from './update/bloodpressure-update.component';
import { BloodpressureDeleteDialogComponent } from './delete/bloodpressure-delete-dialog.component';
import { BloodpressureRoutingModule } from './route/bloodpressure-routing.module';

@NgModule({
  imports: [SharedModule, BloodpressureRoutingModule],
  declarations: [BloodpressureComponent, BloodpressureDetailComponent, BloodpressureUpdateComponent, BloodpressureDeleteDialogComponent],
})
export class BloodpressureModule {}
