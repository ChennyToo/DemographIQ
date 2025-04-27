# DemographIQ Client - Architecture & Development Plan

This document outlines the architectural decisions and development roadmap for the DemographIQ Angular front-end application.

## 1. Core Architecture

demographiq-client/
└── src/
    ├── app/
    │   ├── core/                 # Singleton services, guards, interceptors, core module
    │   │   ├── guards/           # Route guards (e.g., AuthGuard)
    │   │   ├── interceptors/     # HTTP interceptors (e.g., AuthInterceptor, ErrorInterceptor)
    │   │   ├── services/         # App-wide singleton services (AuthService, LoggerService, NotificationService)
    │   ├── features/             # Feature modules (lazy-loaded)
    │   │   ├── map/              # Map feature module
    │   │   │   ├── components/   # Components specific to the map feature
    │   │   │   ├── services/     # Services specific to the map feature
    │   │   │   ├── state/        # Optional: State management for map feature (if using NgRx/Akita)
    │   │   │   └── map.routes.ts # Routes for the map feature
    │   │   │   └── map.component.ts # Entry component for the map feature module
    │   │   ├── dashboard/        # Dashboard feature module
    │   │   │   └── ...           # Similar structure as map
    │   │   ├── records/          # Records/leaderboard feature module
    │   │   │   └── ...           # Similar structure as map
    │   │   └── admin/            # Example: Admin feature module
    │   │       └── ...           # Similar structure as map
    │   ├── layout/               # Layout components (Header, Footer, Sidebar, Main Layout)
    │   │   ├── header/
    │   │   ├── footer/
    │   │   └── main-layout/      # Component orchestrating header/footer/content
    │   ├── shared/               # Reusable UI components, directives, pipes, models, utils
    │   │   ├── components/       # Common UI components (buttons, cards, modals)
    │   │   ├── directives/       # Reusable custom directives
    │   │   ├── pipes/            # Reusable custom pipes
    │   │   ├── models/           # Shared data models/interfaces (can stay top-level if preferred)
    │   │   ├── services/         # Shared utility services (non-singleton, e.g., validation helpers)
    │   │   └── utils/            # Utility functions
    │   ├── app.component.ts      # Root component (often minimal, hosts layout/router-outlet)
    │   ├── app.config.ts         # Application configuration (providers)
    │   └── app.routes.ts         # Top-level application routes (lazy-loading feature modules)
    ├── assets/                 # Static assets (images, fonts, etc.)
    │   ├── i18n/               # Internationalization files (if needed)
    │   └── ...
    ├── environments/           # Environment-specific configuration (dev, prod, staging)
    │   ├── environment.ts
    │   └── environment.prod.ts
    └── styles/                 # Global styles
        ├── base/               # Base styles (reset, typography)
        ├── abstracts/          # Variables, mixins, functions (Sass/SCSS)
        ├── layout/             # Global layout styles
        └── themes/             # Theming files (if applicable)
        └── styles.scss         # Main global stylesheet entry point

-   **`core`**: For essential, app-wide singleton services and logic.
-   **`features`**: Self-contained business capabilities. **Crucially, these will be lazy-loaded.**
-   **`shared`**: Presentation-focused reusable elements. Avoid business logic here.
-   **`layout`**: Defines the overall page structure.

## 2. Rendering Strategy

-   **Initial Phase:** **Client-Side Rendering (CSR)** with **Lazy Loading**.
    -   **Rationale:** Focus on building core features quickly with minimal initial complexity. Lazy loading is essential for performance (especially Time To Interactive - TTI) regardless of the final rendering strategy.
    -   **Priority:** Achieve fast TTI through aggressive lazy loading and optimized client-side code.
-   **Future Phase:** Implement **Server-Side Rendering (SSR)** using Angular Universal (`@angular/ssr`).
    -   **Rationale:** Address Search Engine Optimization (SEO) requirements and potentially improve First Contentful Paint (FCP).
    *   **Prerequisites:** Requires a Node.js hosting environment. Requires code adjustments for platform awareness (`isPlatformBrowser`) and potentially `TransferState`.

## 3. Development Approach & Priorities

1.  **Implement Core Architecture:** Set up the folder structure (`core`, `features`, `shared`, `layout`).
2.  **Build Features with Lazy Loading:** Develop map, records, dashboard, etc., within their respective feature folders and configure `app.routes.ts` for lazy loading immediately.
3.  **Prioritize TTI:** Focus on optimizing the client-side experience and interactivity speed.
4.  **Prepare for SSR:**
    *   Minimize direct use of browser-specific APIs (`window`, `document`, `localStorage`) in components/services. Abstract if necessary.
    *   Keep components focused on presentation; delegate logic to services.
5.  **Implement SSR:** Add SSR via `ng add @angular/ssr` when SEO becomes a primary requirement and infrastructure is ready. Refactor for platform awareness and state transfer as needed.

## 4. Key Decisions Summary

-   **Architecture:** Modular (Core, Features, Shared, Layout).
-   **Rendering:** Start CSR + Lazy Loading -> Add SSR later.
-   **Primary Performance Goal (Initial):** Fast Time To Interactive (TTI).
-   **Primary Performance Goal (Later):** Maintain TTI + Add SEO via SSR.
-   **Complexity Management:** Start simple (CSR), build with SSR compatibility in mind, add SSR incrementally.
