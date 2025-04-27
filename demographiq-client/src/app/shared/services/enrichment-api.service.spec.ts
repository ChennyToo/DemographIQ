import { TestBed } from '@angular/core/testing';

import { EnrichmentApiService } from './enrichment-api.service';

describe('EnrichmentApiService', () => {
  let service: EnrichmentApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EnrichmentApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
