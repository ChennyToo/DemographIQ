import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: '',
        redirectTo: '/mode-select',
        pathMatch: 'full'
    },

    {
        path: 'game',
        loadComponent: () =>
            import('./features/game/game-page/game.component')
                .then(m => m.GameComponent),
        // canActivate: [AuthGuard]
    },

    {
        path: 'mode-select',
        loadComponent: () =>
            import('./features/mode-select/mode-select-page/mode-select.component')
                .then(m => m.ModeSelectComponent),
        // canActivate: [AuthGuard]
    },



    // {
    //     path: '**',
    //     redirectTo: '/landing'
    // }
];