import { Component, Output, EventEmitter, OnInit, OnDestroy, AfterViewInit, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
// Import the TYPE ONLY for type checking in the component
import type * as L from 'leaflet';

@Component({
  selector: 'app-map-display',
  standalone: true,
  imports: [],
  templateUrl: './map-display.component.html',
  styleUrl: './map-display.component.css'
})
export class MapDisplayComponent implements AfterViewInit, OnDestroy {
  @Output() mapClicked = new EventEmitter<{ lat: number, lng: number }>();
  private map: L.Map | null = null;
  private isBrowser: boolean;
  private componentInstanceId = Math.random().toString(36).substring(2, 7); // Keep for potential error logging context

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }


  async ngAfterViewInit(): Promise<void> {
    if (this.isBrowser) { // Check if we are in the browser
      try {
        // Dynamically import Leaflet ONLY in browser
        const leaflet = await import('leaflet');
        // Call initMap with the loaded Leaflet module
        this.initMap(leaflet);
      } catch (error) {
        console.error(`MapDisplay [${this.componentInstanceId}]: Failed to load Leaflet:`, error);
      }
    }
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  // Update initMap signature to accept the dynamically imported Leaflet module type
  private initMap(leaflet: typeof import('leaflet')): void { // Removed useFallbackLayer parameter
    if (this.map) {
      // Avoid re-initializing if already done
      return;
    }

    const mapContainer = document.getElementById('map');
    if (!mapContainer) {
        console.error(`MapDisplay [${this.componentInstanceId}]: Aborting: Map container element #map not found!`);
        return;
    }
    if (mapContainer.classList.contains('leaflet-container')) {
         // Avoid re-initializing if Leaflet already attached
         return;
    }

    try {
        this.map = leaflet.map('map', {
          center: [ 39.8282, -98.5795 ],
          zoom: 4
        });
    } catch (error) {
        console.error(`MapDisplay [${this.componentInstanceId}]: Error during leaflet.map() call:`, error);
        return;
    }

    const customMarkerIcon = leaflet.icon({
      iconUrl: '/assets/map_marker.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34]
    });

    // Directly add the OpenStreetMap tile layer
    leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map!);


    // Set up click listener
    let marker: L.Marker | null = null;
    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const coords = e.latlng;
      this.mapClicked.emit({ lat: coords.lat, lng: coords.lng });

      if (marker) {
        marker.setLatLng(coords);
      } else {
        marker = leaflet.marker(coords, { icon: customMarkerIcon }).addTo(this.map!);
      }
    });
  }
}