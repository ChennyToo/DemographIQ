import { Component, OnInit, OnDestroy } from '@angular/core'; // Removed Input, Output, EventEmitter; Added OnInit, OnDestroy
import { CommonModule } from '@angular/common';
import { GameService } from '../../services/game.service'; // Import GameService
import { Subscription } from 'rxjs'; // Import Subscription

@Component({
  selector: 'app-guess-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './guess-button.component.html',
  styleUrl: './guess-button.component.css'
})
export class GuessButtonComponent implements OnInit, OnDestroy {
  public isLocationSelected: boolean = false;
  private locationSelectedSubscription: Subscription | null = null;

  constructor(public gameService: GameService) {}

  ngOnInit() {
    this.locationSelectedSubscription = this.gameService.isLocationSelected$.subscribe(isSelected => {
      this.isLocationSelected = isSelected;
    });
  }

  ngOnDestroy() {
    this.locationSelectedSubscription?.unsubscribe();
  }

  // The service will handle logic for if the click will make a guess or not
  onClick() {
    this.gameService.onGuessButtonClicked();
  }
}