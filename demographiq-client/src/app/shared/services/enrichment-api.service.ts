import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable, of, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

import { EnrichmentRequest } from '../models/enrichment-request';
import { EnrichmentResponse } from '../models/enrichment-response';

/**
 * Service for handling API calls to the DemographIQ enrichment endpoints
 */
@Injectable({
  providedIn: 'root'
})
export class EnrichmentApiService {
  private baseUrl = 'http://localhost:8080';
  private enrichmentUrl = `${this.baseUrl}/api/enrich`;

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) { }

  /**
   * Submits an enrichment request to get demographic data for a location
   * @param request The enrichment request containing coordinates and data variable
   * @returns Observable of EnrichmentResponse with the demographic data and records
   */
  enrichLocation(request: EnrichmentRequest): Observable<EnrichmentResponse> {
    return this.http.post<EnrichmentResponse>(this.enrichmentUrl, request, this.httpOptions)
      .pipe(
        tap(response => this.log(`Enriched location [${request.latitude}, ${request.longitude}] for ${request.dataVariable}`)),
        catchError(this.handleError<EnrichmentResponse>('enrichLocation'))
      );
  }
  
  /**
   * Handle Http operation that failed.
   * Let the app continue.
   *
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   * @return An Observable that will emit the fallback result value
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      // Log the error to console
      console.error(error);

      // Better error message for user
      this.log(`${operation} failed: ${error.message}`);

      return throwError(() => error);
    };
  }

  /** 
   * Log an EnrichmentApiService message
   * @param message - the message to log
   */
  private log(message: string) {
    console.log(`EnrichmentApiService: ${message}`);
  }
}