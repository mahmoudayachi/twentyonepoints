import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PrefrencesComponent } from './list/prefrences.component';
import { PrefrencesDetailComponent } from './detail/prefrences-detail.component';
import { PrefrencesUpdateComponent } from './update/prefrences-update.component';
import { PrefrencesDeleteDialogComponent } from './delete/prefrences-delete-dialog.component';
import { PrefrencesRoutingModule } from './route/prefrences-routing.module';

@NgModule({
  imports: [SharedModule, PrefrencesRoutingModule],
  declarations: [PrefrencesComponent, PrefrencesDetailComponent, PrefrencesUpdateComponent, PrefrencesDeleteDialogComponent],
})
export class PrefrencesModule {}
