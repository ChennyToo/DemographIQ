import { PastExtremeRecord } from "./past-extreme-record";


export interface ExtremeRecord {
    id?: string;
    variableId: string;
    variableName: string;
    value: number;
    latitude: number;
    longitude: number;
    countryCode: string;
    countryName: string;
    userId: number;
    recordedAt: string; // ISO string format for dates in JSON
    isHigh: boolean;
    previousRecords?: PastExtremeRecord[];
    
    // Optional helper method
    isEmpty?: () => boolean;
  }