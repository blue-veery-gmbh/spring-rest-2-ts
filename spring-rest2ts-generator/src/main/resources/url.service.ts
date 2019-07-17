import { Inject, Injectable } from '@angular/core';

@Injectable()
export class UrlService {
  backendUrl: string;

  public constructor(@Inject('BACKEND_URL')
                       backendUrl: string) {
    this.backendUrl = backendUrl;
  }

  public getBackendUrl(): string {
    return this.backendUrl;
  }

}

