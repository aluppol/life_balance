import { render } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';
import { createMemoryRouter } from 'react-router';
import { vi } from 'vitest';
import { LifeBalanceApp } from '../app/LifeBalanceApp';
import { createQueryClient } from '../app/queryClient';
import { routes } from '../app/routes';

export function renderRoute(path: string) {
  const router = createMemoryRouter(routes, { initialEntries: [path] });
  const user = userEvent.setup();
  render(<LifeBalanceApp router={router} queryClient={createQueryClient()} />);
  return { user, router };
}

export function stubPageReload() {
  const reload = vi.fn();
  const { href, origin } = window.location;
  vi.stubGlobal('location', { href, origin, reload });
  return reload;
}
