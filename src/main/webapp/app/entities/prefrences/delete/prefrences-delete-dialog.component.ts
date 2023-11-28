import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPrefrences } from '../prefrences.model';
import { PrefrencesService } from '../service/prefrences.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './prefrences-delete-dialog.component.html',
})
export class PrefrencesDeleteDialogComponent {
  prefrences?: IPrefrences;

  constructor(protected prefrencesService: PrefrencesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.prefrencesService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
