import { Component, Output, EventEmitter, OnInit, OnDestroy, AfterViewInit, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
// Import the TYPE ONLY for type checking in the component
import type * as Leaflet from 'leaflet';
import { GameService } from '../../services/game.service';
import { Subscription } from 'rxjs';
type LeafletModule = typeof import('leaflet');

@Component({
  selector: 'app-map-display',
  standalone: true,
  imports: [],
  templateUrl: './map-display.component.html',
  styleUrl: './map-display.component.css'
})
export class MapDisplayComponent implements AfterViewInit, OnDestroy {
  private marker: L.Marker | null = null;
  private map: Leaflet.Map | null = null;
  private isBrowser: boolean;
  private resetSubscription: Subscription | null = null;

  constructor(@Inject(PLATFORM_ID) private platformId: Object, private gameService: GameService) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    this.resetSubscription = this.gameService.resetMarker$.subscribe(() => {
      this.removeMarker();
    });
  }

  async ngAfterViewInit(): Promise<void> {
    if (this.isBrowser) {
      try {
        const leaflet = await import('leaflet');
        this.initMap(leaflet);
      } catch (error) {
        console.error("MapDisplay : ", error);
      }
    }
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  private initMap(leaflet: LeafletModule) {
    this.createMapInstance(leaflet);
    this.setTileLayer(leaflet);
    this.setupClickListener(leaflet);
  }

  private createMapInstance(leaflet: LeafletModule) {
    try {
      this.map = leaflet.map('map', {
        center: [40, 0],
        zoom: 3
      });
    } catch (error) {
      console.error(`MapDisplay : Error during leaflet.map() call:`, error);
    }
  }

  private setTileLayer(leaflet: LeafletModule) {
    //OpenStreetMap tile layer
    leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map!);
  }

  private setupClickListener(leaflet: LeafletModule) {
    if (!this.map) return;
    const marketIconSizePx = 40;
    const customMarkerIcon = leaflet.icon({
      iconUrl: '/assets/map_marker.png',
      iconSize: [marketIconSizePx, marketIconSizePx],
      iconAnchor: [(marketIconSizePx / 2), marketIconSizePx],
    });

    this.map.on('click', (event: L.LeafletMouseEvent) => {
      const coords = event.latlng;
      this.gameService.setSelectedLocation({ latitude: coords.lat, longitude: coords.lng });
      if (this.marker) {
        this.marker.setLatLng(coords);
      } else {
        this.marker = leaflet.marker(coords, { icon: customMarkerIcon }).addTo(this.map!);
      }
    });
  }

  private removeMarker() {
    if (this.marker && this.map) {
      this.map.removeLayer(this.marker);
      this.marker = null;
      console.log("MapDisplay: Marker removed.");
    }
  }
}