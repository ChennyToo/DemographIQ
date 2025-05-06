import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameModeCardComponent } from './game-mode-card.component';

describe('GameModeCardComponent', () => {
  let component: GameModeCardComponent;
  let fixture: ComponentFixture<GameModeCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameModeCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GameModeCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
