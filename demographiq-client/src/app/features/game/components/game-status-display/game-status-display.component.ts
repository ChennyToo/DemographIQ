import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { map, catchError, startWith } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { GameService } from '../../services/game.service';
import { MetricNameConverterService } from '../../../../shared/services/metric-name-converter.service';
import { CountyNameConverterService } from '../../../../shared/services/county-name-converter.service';

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

  constructor(private gameService: GameService, private metricNameConverterService: MetricNameConverterService, private countyNameConverterService: CountyNameConverterService  ) {
    this.currentRound$ = this.gameService.currentRound$;
    this.totalRounds$ = this.gameService.totalRounds$;
    this.score$ = this.gameService.score$;
    this.isHigh$ = this.gameService.isHigh$;
    this.gameMode$ = this.gameService.gameMode$.pipe(
      map(gameModeCode => {
        try {
          return this.countyNameConverterService.getCountryName(gameModeCode);
        } catch (error) {
          console.error("Error converting game mode code to name:", error);
          return 'Unknown Mode';
        }
      }),
      startWith('Loading Mode...')
    );
    this.metric$ = this.gameService.metric$.pipe(
      map(metricId => {
        try {
          return this.metricNameConverterService.getMetricName(metricId);
        } catch (error) {
          console.error("Error converting metric ID to name: ", error);
          return 'Unknown Metric';
        }
      }),
      startWith('Loading Metric...')
    );
  }
}
