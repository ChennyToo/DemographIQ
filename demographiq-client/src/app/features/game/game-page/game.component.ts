import { Component, ViewChild } from '@angular/core';
import { MapDisplayComponent } from '../components/map-display/map-display.component';
import { GameStatusDisplayComponent } from '../components/game-status-display/game-status-display.component';
import { GuessButtonComponent } from '../components/guess-button/guess-button.component';

@Component({
  selector: 'app-game',
  imports: [
    MapDisplayComponent,
    GameStatusDisplayComponent,
    GuessButtonComponent,
  ],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css',
})
export class GameComponent {
    public isLocationSelected: boolean = false;
    public selectedCoordinates: { latitude: number; longitude: number } | null = null;
    @ViewChild(MapDisplayComponent, { static: false }) mapDisplayComponent!: MapDisplayComponent;


    onMapClicked(event: { latitude: number; longitude: number }) {
      this.isLocationSelected = true;
      this.selectedCoordinates = event;
    }

    handleGuess() {
      if (this.selectedCoordinates) {
        console.log('Guessing with coordinates:', this.selectedCoordinates);
        this.mapDisplayComponent.removeMarker();
        this.isLocationSelected = false;
        this.selectedCoordinates = null;

      }
    }
}
