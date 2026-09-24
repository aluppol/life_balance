import { expect, test } from 'vitest';
import { focusOnMount } from './focus';

test('focuses an element once it is mounted and ignores its removal', () => {
  const heading = document.createElement('h1');
  heading.tabIndex = -1;
  document.body.append(heading);
  focusOnMount(heading);
  expect(heading).toHaveFocus();
  expect(() => focusOnMount(null)).not.toThrow();
  heading.remove();
});
