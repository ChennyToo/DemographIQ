import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-game-mode-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game-mode-card.component.html',
  styleUrls: ['./game-mode-card.component.css']
})
export class GameModeCardComponent {
  // Input property for the country code (e.g., 'US', 'CA', 'WORLD')
  @Input() countryCode: string = '';
  // Input property for the display name (e.g., 'United States', 'Canada', 'Global')
  @Input() countryName: string = '';

  // Output event emitter for when the mode is selected
  @Output() modeSelected = new EventEmitter<string>();

  constructor() { }

  /**
   * Emits the modeSelected event with the countryCode when the button is clicked.
   */
  selectMode(): void {
    if (this.countryCode) {
      this.modeSelected.emit(this.countryCode);
    } else {
      console.warn('GameModeCardComponent: countryCode is not set.');
    }
  }
}