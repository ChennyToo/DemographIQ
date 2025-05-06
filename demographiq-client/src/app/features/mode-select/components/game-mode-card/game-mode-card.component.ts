import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-game-mode-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game-mode-card.component.html',
  styleUrls: ['./game-mode-card.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GameModeCardComponent implements OnInit{
  // Input property for the country code (e.g., 'US', 'CA', 'WORLD')
  @Input() countryCode: string = '';
  // Input property for the display name (e.g., 'United States', 'Canada', 'Global')
  @Input() countryName: string = '';

  private imageBasePath = 'assets/country-images/';
  private imageExtension = '.jpg';
  public backgroundImageUrl: string = '';
  // Output event emitter for when the mode is selected
  @Output() modeSelected = new EventEmitter<string>();

  constructor() { }

  ngOnInit(): void {
    this.updateBackgroundImageUrl();
  }

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

  private updateBackgroundImageUrl(): void {
    const imageUrl = `${this.imageBasePath}${this.countryCode.toUpperCase()}${this.imageExtension}`;
    this.backgroundImageUrl = `url('${imageUrl}')`;
  }
}