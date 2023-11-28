import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PrefrencesFormService, PrefrencesFormGroup } from './prefrences-form.service';
import { IPrefrences } from '../prefrences.model';
import { PrefrencesService } from '../service/prefrences.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { units } from 'app/entities/enumerations/units.model';

@Component({
  selector: 'jhi-prefrences-update',
  templateUrl: './prefrences-update.component.html',
})
export class PrefrencesUpdateComponent implements OnInit {
  isSaving = false;
  prefrences: IPrefrences | null = null;
  unitsValues = Object.keys(units);

  usersSharedCollection: IUser[] = [];

  editForm: PrefrencesFormGroup = this.prefrencesFormService.createPrefrencesFormGroup();

  constructor(
    protected prefrencesService: PrefrencesService,
    protected prefrencesFormService: PrefrencesFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ prefrences }) => {
      this.prefrences = prefrences;
      if (prefrences) {
        this.updateForm(prefrences);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const prefrences = this.prefrencesFormService.getPrefrences(this.editForm);
    if (prefrences.id !== null) {
      this.subscribeToSaveResponse(this.prefrencesService.update(prefrences));
    } else {
      this.subscribeToSaveResponse(this.prefrencesService.create(prefrences));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPrefrences>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(prefrences: IPrefrences): void {
    this.prefrences = prefrences;
    this.prefrencesFormService.resetForm(this.editForm, prefrences);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      prefrences.userprefrences
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.prefrences?.userprefrences)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
