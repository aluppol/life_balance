import { screen, within } from '@testing-library/react';
import { http, HttpResponse } from 'msw';
import { expect, test } from 'vitest';
import { planner } from '../test/planner';
import { renderRoute, stubPageReload } from '../test/renderRoute';
import { server } from '../test/server';

test('shows the product, the navigation and the signed-in person', async () => {
  renderRoute('/');
  expect(screen.getByRole('link', { name: 'Life Balance' })).toHaveAttribute('href', '/');
  const navigation = screen.getByRole('navigation', { name: 'Main' });
  expect(
    within(navigation)
      .getAllByRole('link')
      .map((link) => link.textContent),
  ).toEqual(['Compass', 'Mission', 'Values', 'Roles', 'Goals', 'Week', 'Day', 'Review']);
  expect(
    within(navigation)
      .getAllByRole('link')
      .map((link) => link.getAttribute('href')),
  ).toEqual(['/', '/mission', '/values', '/roles', '/goals', '/week', '/day', '/review']);
  expect(await screen.findByText('Ada Lovelace')).toBeInTheDocument();
  expect(screen.getByText('Signed in as')).toBeInTheDocument();
  expect(screen.getByRole('link', { name: 'Sign out' })).toHaveAttribute(
    'href',
    '/oauth2/sign_out',
  );
  expect(screen.queryByRole('complementary', { name: 'Demo account' })).not.toBeInTheDocument();
});

test('marks the section of the current page in the navigation', async () => {
  renderRoute('/week/2026-09-21');
  const navigation = screen.getByRole('navigation', { name: 'Main' });
  expect(within(navigation).getByRole('link', { name: 'Week' })).toHaveAttribute(
    'aria-current',
    'page',
  );
  expect(within(navigation).getByRole('link', { name: 'Compass' })).not.toHaveAttribute(
    'aria-current',
  );
  expect(await screen.findByRole('heading', { level: 1 })).toHaveTextContent(
    'Week of September 21, 2026',
  );
});

test('tells a guest that the account is a shared demo', async () => {
  planner().signedInPerson = { displayName: 'Guest', isGuest: true };
  renderRoute('/');
  expect(await screen.findByRole('complementary', { name: 'Demo account' })).toHaveTextContent(
    'You are exploring a shared demo account. Feel free to change anything — it resets every night.',
  );
});

test('reloads the page to sign in again when the session has ended', async () => {
  const reload = stubPageReload();
  server.use(http.get('/api/values', () => new HttpResponse(null, { status: 401 })));
  renderRoute('/values');
  expect(
    await screen.findByText('Your session has ended. Reload the page to sign in again.'),
  ).toBeInTheDocument();
  expect(reload).toHaveBeenCalledTimes(1);
});

test('answers an unknown address with a way back home', async () => {
  const { user, router } = renderRoute('/nowhere');
  expect(screen.getByRole('heading', { level: 1, name: 'Page not found' })).toBeInTheDocument();
  await user.click(screen.getByRole('link', { name: 'Go back to your compass' }));
  expect(router.state.location.pathname).toBe('/');
  expect(await screen.findByRole('heading', { level: 1, name: 'Compass' })).toBeInTheDocument();
});

test('offers a skip link to the main content', () => {
  renderRoute('/');
  expect(screen.getByRole('link', { name: 'Skip to content' })).toHaveAttribute(
    'href',
    '#main-content',
  );
  expect(screen.getByRole('main')).toHaveAttribute('id', 'main-content');
  expect(screen.getByRole('main')).toHaveAttribute('tabindex', '-1');
});

test('moves the focus to the heading of the page it navigates to', async () => {
  const { user } = renderRoute('/');
  const navigation = screen.getByRole('navigation', { name: 'Main' });
  await user.click(within(navigation).getByRole('link', { name: 'Roles' }));
  const heading = await screen.findByRole('heading', { level: 1, name: 'Roles' });
  expect(heading).toHaveFocus();
  expect(heading).toHaveAttribute('tabindex', '-1');
  expect(document.title).toBe('Roles · Life Balance');
});
