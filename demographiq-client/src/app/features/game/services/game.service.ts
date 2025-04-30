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
  private readonly _gameMode = new BehaviorSubject<string>("WORLD");

  readonly score$: Observable<number> = this._score.asObservable();
  readonly selectedLocation$: Observable<Coordinates | null> = this._selectedLocation.asObservable();
  readonly isLocationSelected$: Observable<boolean> = this._isLocationSelected.asObservable();
  readonly resetMarker$: Observable<void> = this._resetMarker.asObservable();
  readonly currentRound$: Observable<number> = this._currentRound.asObservable();
  readonly totalRounds$: Observable<number> = this._totalRounds.asObservable();
  readonly gameMode$: Observable<string> = this._gameMode.asObservable();

  constructor(private enrichmentApiService: EnrichmentApiService) { }


  startGame(gameMode: string = "WORLD", totalRounds: number = 5): void {
    console.log(`GameService: Starting new game. Mode: ${gameMode}, Rounds: ${totalRounds}`);
    this._gameMode.next(gameMode);
    this._totalRounds.next(totalRounds);
    this._currentRound.next(1);
    this._score.next(0);
    this.resetSelection();
  }

  private advanceRound() {
    const currentRound = this._currentRound.getValue();
    const totalRounds = this._totalRounds.getValue();

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
    const currentDataVariable = 'POPDENS_CY';
    const currentSourceCountry = 'WORLD';
    const currentUserId = 123;
    const isGuessHigh = true;
    const enrichmentRequest: EnrichmentRequest = {
      latitude: coordinates.latitude,
      longitude: coordinates.longitude,
      sourceCountry: currentSourceCountry,
      userId: currentUserId,
      dataVariable: currentDataVariable,
      isHigh: isGuessHigh
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


}