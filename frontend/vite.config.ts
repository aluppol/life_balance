import react from '@vitejs/plugin-react';
import { defineConfig } from 'vitest/config';
import { accessTokenForwarding } from './dev/accessTokenForwarding.ts';

const backendOrigin = 'http://localhost:8080';
const backendPaths = ['/api', '/actuator', '/v3', '/swagger-ui', '/swagger-ui.html'];

export default defineConfig({
  base: '/',
  plugins: [react(), accessTokenForwarding(backendPaths)],
  server: {
    port: 5173,
    strictPort: true,
    proxy: Object.fromEntries(backendPaths.map((path) => [path, backendOrigin])),
  },
  build: {
    sourcemap: false,
  },
  test: {
    environment: 'jsdom',
    include: ['src/**/*.test.{ts,tsx}'],
    setupFiles: ['./src/test/setup.ts'],
    testTimeout: 15_000,
    env: { TZ: 'America/Chicago' },
    coverage: {
      provider: 'v8',
      include: ['src/**/*.{ts,tsx}'],
      exclude: ['src/main.tsx', 'src/test/**', 'src/**/*.test.{ts,tsx}'],
      reporter: ['text', 'html'],
      thresholds: { statements: 90, lines: 90, functions: 90, branches: 85 },
    },
  },
});
