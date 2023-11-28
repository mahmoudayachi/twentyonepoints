import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PrefrencesFormService } from './prefrences-form.service';
import { PrefrencesService } from '../service/prefrences.service';
import { IPrefrences } from '../prefrences.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { PrefrencesUpdateComponent } from './prefrences-update.component';

describe('Prefrences Management Update Component', () => {
  let comp: PrefrencesUpdateComponent;
  let fixture: ComponentFixture<PrefrencesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let prefrencesFormService: PrefrencesFormService;
  let prefrencesService: PrefrencesService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PrefrencesUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PrefrencesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PrefrencesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    prefrencesFormService = TestBed.inject(PrefrencesFormService);
    prefrencesService = TestBed.inject(PrefrencesService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const prefrences: IPrefrences = { id: 456 };
      const userprefrences: IUser = { id: 2934 };
      prefrences.userprefrences = userprefrences;

      const userCollection: IUser[] = [{ id: 77300 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [userprefrences];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ prefrences });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const prefrences: IPrefrences = { id: 456 };
      const userprefrences: IUser = { id: 97815 };
      prefrences.userprefrences = userprefrences;

      activatedRoute.data = of({ prefrences });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(userprefrences);
      expect(comp.prefrences).toEqual(prefrences);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPrefrences>>();
      const prefrences = { id: 123 };
      jest.spyOn(prefrencesFormService, 'getPrefrences').mockReturnValue(prefrences);
      jest.spyOn(prefrencesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ prefrences });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: prefrences }));
      saveSubject.complete();

      // THEN
      expect(prefrencesFormService.getPrefrences).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(prefrencesService.update).toHaveBeenCalledWith(expect.objectContaining(prefrences));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPrefrences>>();
      const prefrences = { id: 123 };
      jest.spyOn(prefrencesFormService, 'getPrefrences').mockReturnValue({ id: null });
      jest.spyOn(prefrencesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ prefrences: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: prefrences }));
      saveSubject.complete();

      // THEN
      expect(prefrencesFormService.getPrefrences).toHaveBeenCalled();
      expect(prefrencesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPrefrences>>();
      const prefrences = { id: 123 };
      jest.spyOn(prefrencesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ prefrences });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(prefrencesService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
