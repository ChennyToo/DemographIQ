import { Component, OnInit } from '@angular/core'; // Removed ViewChild; Added OnInit
import { CommonModule } from '@angular/common'; // Import CommonModule
import { MapDisplayComponent } from '../components/map-display/map-display.component';
import { GameStatusDisplayComponent } from '../components/game-status-display/game-status-display.component';
import { GuessButtonComponent } from '../components/guess-button/guess-button.component';
import { GameService } from '../services/game.service'; // Import GameService

@Component({
  selector: 'app-game',
  standalone: true, // Ensure standalone is true if using it
  imports: [
    CommonModule, // Add CommonModule
    MapDisplayComponent,
    GameStatusDisplayComponent,
    GuessButtonComponent,
  ],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css',
})
export class GameComponent {
    constructor(private gameService: GameService) {}
}