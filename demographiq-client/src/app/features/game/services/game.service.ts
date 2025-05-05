import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { EnrichmentApiService } from '../../../shared/services/enrichment-api.service';
import { EnrichmentRequest } from '../../../shared/models/enrichment-request';

interface Coordinates {
  latitude: number;
  longitude: number;
}

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private readonly _score = new BehaviorSubject<number>(0);
  private readonly _selectedLocation = new BehaviorSubject<Coordinates | null>(null);
  private readonly _isLocationSelected = new BehaviorSubject<boolean>(false);
  private readonly _resetMarker = new Subject<void>();
  private readonly _currentRound = new BehaviorSubject<number>(1);
  private readonly _totalRounds = new BehaviorSubject<number>(5);
  private readonly _gameMode = new BehaviorSubject<string>("WORLD"); // 2 Character ISO identifier or "WORLD"
  private readonly _metric = new BehaviorSubject<string>("POPDENS_CY");
  private readonly _isHigh = new BehaviorSubject<boolean>(false);


  readonly score$: Observable<number> = this._score.asObservable();
  readonly selectedLocation$: Observable<Coordinates | null> = this._selectedLocation.asObservable();
  readonly isLocationSelected$: Observable<boolean> = this._isLocationSelected.asObservable();
  readonly resetMarker$: Observable<void> = this._resetMarker.asObservable();
  readonly currentRound$: Observable<number> = this._currentRound.asObservable();
  readonly totalRounds$: Observable<number> = this._totalRounds.asObservable();
  readonly gameMode$: Observable<string> = this._gameMode.asObservable();
  readonly metric$: Observable<string> = this._metric.asObservable();
  readonly isHigh$: Observable<boolean> = this._isHigh.asObservable();


  constructor(private enrichmentApiService: EnrichmentApiService) { }


  startGame(gameMode: string = "WORLD", totalRounds: number = 5) {
    console.log(`GameService: Starting new game. Mode: ${gameMode}, Rounds: ${totalRounds}`);
    this._metric.next("POPDENS_CY");
    this._gameMode.next(gameMode);
    this._totalRounds.next(totalRounds);
    this._currentRound.next(1);
    this._score.next(0);
    this.randomizeIsHigh();
    this.resetSelection();
  }

  private advanceRound() {
    const currentRound = this._currentRound.getValue();
    const totalRounds = this._totalRounds.getValue();
    this.randomizeIsHigh();

    if (currentRound < totalRounds) {
      this._currentRound.next(currentRound + 1);
      console.log(`GameService: Advancing to round ${currentRound + 1}`);
    } else {
      console.log(`GameService: Game over. Resetting.`);
      this.startGame(this._gameMode.getValue(), this._totalRounds.getValue());
    }
  }


  setSelectedLocation(coordinates: Coordinates) {
    console.log('GameService: Setting selected location', coordinates);
    this._selectedLocation.next(coordinates);
    this._isLocationSelected.next(true);
  }

  onGuessButtonClicked() {
    const selectedCoordinates = this._selectedLocation.getValue();
    if (selectedCoordinates) {
      this.makeGuess(selectedCoordinates);
    }
  }

  makeGuess(coordinates: Coordinates) {
    console.log('GameService: Guess made for location:', coordinates);
    this._resetMarker.next();
    this.resetSelection();
    const currentSourceCountry = 'WORLD';
    const currentUserId = 123;
    const enrichmentRequest: EnrichmentRequest = {
      latitude: coordinates.latitude,
      longitude: coordinates.longitude,
      sourceCountry: currentSourceCountry,
      userId: currentUserId,
      dataVariable: this._metric.getValue(),
      isHigh: this._isHigh.getValue(),
    };
    this.enrichmentApiService.enrichLocation(enrichmentRequest).subscribe({
      next: response => {
        console.log('Enrichment response:', response);
        this.advanceRound();
        this._score.next(this._score.getValue() + response.score!);
      },
      error: error => {
        console.error('Error during enrichment:', error);
      },

    });
  }

  private resetSelection() {
    this._selectedLocation.next(null);
    this._isLocationSelected.next(false);
  }

  private randomizeIsHigh() {
    this._isHigh.next(Math.random() < 0.5);
  }


}