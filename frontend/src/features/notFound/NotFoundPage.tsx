import { Link } from 'react-router';
import { Page } from '../../shared/ui/Page';

export function NotFoundPage() {
  return (
    <Page title="Page not found" intro="There is nothing at this address.">
      <p>
        <Link to="/">Go back to your compass</Link>
      </p>
    </Page>
  );
}
