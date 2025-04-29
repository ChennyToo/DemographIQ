import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-guess-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './guess-button.component.html',
  styleUrl: './guess-button.component.css'
})
export class GuessButtonComponent {
  @Input() isLocationSelected: boolean = false;
  @Output() buttonClicked = new EventEmitter<void>();
  onButtonClick(): void {
    if (this.isLocationSelected) {
      this.buttonClicked.emit();
    }
  }

}