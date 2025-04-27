import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameStatusDisplayComponent } from './game-status-display.component';

describe('GameStatusDisplayComponent', () => {
  let component: GameStatusDisplayComponent;
  let fixture: ComponentFixture<GameStatusDisplayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameStatusDisplayComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GameStatusDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
