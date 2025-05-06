import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MapDisplayComponent } from '../components/map-display/map-display.component';
import { GameStatusDisplayComponent } from '../components/game-status-display/game-status-display.component';
import { GuessButtonComponent } from '../components/guess-button/guess-button.component';

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [
    CommonModule,
    MapDisplayComponent,
    GameStatusDisplayComponent,
    GuessButtonComponent,
  ],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css',
})
export class GameComponent {}
