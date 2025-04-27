export interface EnrichmentRequest {
    latitude: number;
    longitude: number;
    sourceCountry: string;
    userId: number;
    dataVariable: string;
    isHigh: boolean;
  }