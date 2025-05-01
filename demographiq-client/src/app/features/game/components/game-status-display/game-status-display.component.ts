import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { GameService } from '../../services/game.service';

@Component({
  selector: 'app-game-status-display',
  imports: [CommonModule],
  templateUrl: './game-status-display.component.html',
  styleUrl: './game-status-display.component.css'
})
export class GameStatusDisplayComponent {
  currentRound$: Observable<number>;
  totalRounds$: Observable<number>;
  gameMode$: Observable<string>;
  score$: Observable<number>;
  metric$: Observable<string>;
  isHigh$: Observable<boolean>;

  constructor(private gameService: GameService) {
    // Assign observables in the constructor
    this.currentRound$ = this.gameService.currentRound$;
    this.totalRounds$ = this.gameService.totalRounds$;
    this.gameMode$ = this.gameService.gameMode$;
    this.score$ = this.gameService.score$;
    this.metric$ = this.gameService.metric$;
    this.isHigh$ = this.gameService.isHigh$;
  }
}
