import { Component } from '@angular/core';
import { MapDisplayComponent } from "../components/map-display/map-display.component";

@Component({
  selector: 'app-game',
  imports: [MapDisplayComponent],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css'
})
export class GameComponent {

}
