import { TestBed } from '@angular/core/testing';

import { MetricNameConverterService } from './metric-name-converter.service';

describe('MetricNameConverterService', () => {
  let service: MetricNameConverterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MetricNameConverterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
