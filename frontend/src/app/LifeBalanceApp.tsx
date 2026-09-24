import { type QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { type createBrowserRouter, RouterProvider } from 'react-router';

interface LifeBalanceAppProps {
  readonly router: ReturnType<typeof createBrowserRouter>;
  readonly queryClient: QueryClient;
}

export function LifeBalanceApp({ router, queryClient }: LifeBalanceAppProps) {
  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  );
}
