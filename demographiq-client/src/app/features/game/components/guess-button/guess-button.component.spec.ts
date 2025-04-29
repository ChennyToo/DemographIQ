import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuessButtonComponent } from './guess-button.component';

describe('GuessButtonComponent', () => {
  let component: GuessButtonComponent;
  let fixture: ComponentFixture<GuessButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuessButtonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GuessButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
