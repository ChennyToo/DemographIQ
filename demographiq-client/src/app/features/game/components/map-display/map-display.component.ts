import { Component, Output, EventEmitter, OnInit, OnDestroy, AfterViewInit, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
// Import the TYPE ONLY for type checking in the component
import type * as Leaflet from 'leaflet';
type LeafletModule = typeof import('leaflet');

@Component({
  selector: 'app-map-display',
  standalone: true,
  imports: [],
  templateUrl: './map-display.component.html',
  styleUrl: './map-display.component.css'
})
export class MapDisplayComponent implements AfterViewInit, OnDestroy {
  @Output() mapClicked = new EventEmitter<{ latitude: number, longitude: number }>();
  private map: Leaflet.Map | null = null;
  private isBrowser: boolean;

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  async ngAfterViewInit(): Promise<void> {
    if (this.isBrowser) {
      try {
        const leaflet = await import('leaflet');
        this.initMap(leaflet);
      } catch (error) {
        console.error(`MapDisplay : Failed to load Leaflet:`, error);
      }
    }
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  private initMap(leaflet: LeafletModule): void {
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

  private setTileLayer(leaflet: LeafletModule): void {
    //OpenStreetMap tile layer
    leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map!);
  }

  private setupClickListener(leaflet: LeafletModule) {
    const marketIconSizePx = 40;
    const customMarkerIcon = leaflet.icon({
      iconUrl: '/assets/map_marker.png',
      iconSize: [marketIconSizePx, marketIconSizePx],
      iconAnchor: [(marketIconSizePx / 2), marketIconSizePx],
    });

    let marker: L.Marker | null = null;
    this.map!.on('click', (event: L.LeafletMouseEvent) => {
      const coords = event.latlng;
      this.mapClicked.emit({ latitude: coords.lat, longitude: coords.lng });
      if (marker) { // If the market already is on map, just update its position
        marker.setLatLng(coords);
      } else {
        marker = leaflet.marker(coords, { icon: customMarkerIcon }).addTo(this.map!);
      }
    });
  }
}