import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { createBrowserRouter } from 'react-router';
import './app/global.css';
import { LifeBalanceApp } from './app/LifeBalanceApp';
import { createQueryClient } from './app/queryClient';
import { routes } from './app/routes';

const container = document.getElementById('root');

if (container !== null) {
  createRoot(container).render(
    <StrictMode>
      <LifeBalanceApp router={createBrowserRouter(routes)} queryClient={createQueryClient()} />
    </StrictMode>,
  );
}
