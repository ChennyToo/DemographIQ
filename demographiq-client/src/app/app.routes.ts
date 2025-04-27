import { Routes } from '@angular/router';

export const routes: Routes = [
    // Redirect the empty path ('/') to the '/game' path
    {
        path: '',
        redirectTo: '/game',
        pathMatch: 'full' // Important: ensures the whole path matches ''
    },

    // Define the route for '/game'
    {
        path: 'game',
        // Lazy-load the GameComponent when the '/game' path is activated
        loadComponent: () =>
            import('./features/game/game-page/game.component') // Use the corrected path after renaming game/game to game/page
                .then(m => m.GameComponent),
        // Optional: Add route guards later if login is required
        // canActivate: [AuthGuard]
    },


    // {
    //     path: '**',
    //     redirectTo: '/landing'
    // }
];