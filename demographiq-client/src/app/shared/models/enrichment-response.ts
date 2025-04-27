import { ExtremeRecord } from "./extreme-record";

export interface EnrichmentResponse {
    sourceCountry: string;
    variableId: string;
    value: number;
    score?: number;
    currentRecord: ExtremeRecord;
  }