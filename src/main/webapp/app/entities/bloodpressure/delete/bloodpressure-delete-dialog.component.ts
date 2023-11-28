import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBloodpressure } from '../bloodpressure.model';
import { BloodpressureService } from '../service/bloodpressure.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './bloodpressure-delete-dialog.component.html',
})
export class BloodpressureDeleteDialogComponent {
  bloodpressure?: IBloodpressure;

  constructor(protected bloodpressureService: BloodpressureService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bloodpressureService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
