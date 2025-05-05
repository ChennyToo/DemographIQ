import { TestBed } from '@angular/core/testing';

import { CountyNameConverterService } from './county-name-converter.service';

describe('CountyNameConverterService', () => {
  let service: CountyNameConverterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CountyNameConverterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
