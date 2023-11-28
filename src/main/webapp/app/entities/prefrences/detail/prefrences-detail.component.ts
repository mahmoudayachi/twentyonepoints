import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPrefrences } from '../prefrences.model';

@Component({
  selector: 'jhi-prefrences-detail',
  templateUrl: './prefrences-detail.component.html',
})
export class PrefrencesDetailComponent implements OnInit {
  prefrences: IPrefrences | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ prefrences }) => {
      this.prefrences = prefrences;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
