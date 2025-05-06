import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { GameModeCardComponent } from '../components/game-mode-card/game-mode-card.component';
import { GameService } from '../../game/services/game.service';

interface GameMode {
  code: string;
  name: string;
}

@Component({
  selector: 'app-mode-select',
  standalone: true,
  imports: [CommonModule, GameModeCardComponent],
  templateUrl: './mode-select.component.html',
  styleUrls: ['./mode-select.component.css']
})
export class ModeSelectComponent {

  gameModes: GameMode[] = [
    { code: 'WORLD', name: 'Global' },
    { code: 'US', name: 'United States' },
    { code: 'CA', name: 'Canada' },
    { code: 'CN', name: 'China' },
    { code: 'GB', name: 'United Kingdom' },
    { code: 'DE', name: 'Germany' }
  ];

  constructor(
    private router: Router,
    private gameService: GameService
  ) {}

  /**
   * Handles the mode selection from a card component.
   * Starts the game with the selected mode and navigates to the game page.
   * @param modeCode The country code selected (e.g., 'US')
   */
  onModeSelected(modeCode: string): void {
    console.log(`Mode selected: ${modeCode}`);
    this.gameService.startGame(modeCode);
    this.router.navigate(['/game']);
  }
}