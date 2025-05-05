import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MetricNameConverterService {

  private static readonly METRIC_ID_TO_NAME: ReadonlyMap<string, string> = new Map<string, string>([
    ["POPDENS_CY", "Population Density"]
  ]);

  constructor() { }

  /**
   * Converts a metric ID to its corresponding human-readable name.
   *
   * @param metricId The metric identifier (e.g., "POPDENS_CY")
   * @returns The human-readable name of the metric (e.g., "Population Density")
   * @throws Error if the metric ID is null/undefined or not found in the map
   */
  getMetricName(metricId: string | null | undefined): string {
    if (metricId == null || metricId == undefined) {
      const errorMessage = "Metric ID cannot be null or undefined";
      console.error(`MetricNameConverterService: ${errorMessage}`);
      throw new Error(errorMessage);
    }

    if (!MetricNameConverterService.METRIC_ID_TO_NAME.has(metricId)) {
      const errorMessage = `Unknown metric ID: ${metricId}`;
      console.error(`MetricNameConverterService: ${errorMessage}`);
      throw new Error(errorMessage);
    }

    const metricName = MetricNameConverterService.METRIC_ID_TO_NAME.get(metricId);

    return metricName!;
  }
}