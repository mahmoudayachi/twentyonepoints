import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBloodpressure } from '../bloodpressure.model';

@Component({
  selector: 'jhi-bloodpressure-detail',
  templateUrl: './bloodpressure-detail.component.html',
})
export class BloodpressureDetailComponent implements OnInit {
  bloodpressure: IBloodpressure | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bloodpressure }) => {
      this.bloodpressure = bloodpressure;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
