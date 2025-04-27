# Environments

This directory contains environment-specific configuration files for the Angular application.

Typically, this includes:

-   `environment.ts`: Default configuration used for development builds (`ng serve` or `ng build`).
-   `environment.prod.ts`: Configuration used for production builds (`ng build --configuration production`).

These files allow different settings (like API endpoints, feature flags, logging levels) to be used depending on the build target without changing the application code itself. The Angular CLI replaces the contents of `environment.ts` with the target-specific file during the build process.