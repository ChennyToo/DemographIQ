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
  private readonly _selectedLocation = new BehaviorSubject<Coordinates | null>(null);
  private readonly _isLocationSelected = new BehaviorSubject<boolean>(false);
  private readonly _resetMarker = new Subject<void>();

  readonly selectedLocation$: Observable<Coordinates | null> = this._selectedLocation.asObservable();
  readonly isLocationSelected$: Observable<boolean> = this._isLocationSelected.asObservable();
  readonly resetMarker$: Observable<void> = this._resetMarker.asObservable();

  constructor(private enrichmentApiService: EnrichmentApiService) { }
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
    const currentSourceCountry = 'US';
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