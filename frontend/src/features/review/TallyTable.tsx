import styles from './Review.module.css';
import type { TallyRow } from './scorecardRows';

interface TallyTableProps {
  readonly caption: string;
  readonly heading: string;
  readonly rows: readonly TallyRow[];
}

export function TallyTable({ caption, heading, rows }: TallyTableProps) {
  return (
    <table className={styles.table}>
      <caption className={styles.caption}>{caption}</caption>
      <thead>
        <tr>
          <th scope="col">{heading}</th>
          <th scope="col">Planned</th>
          <th scope="col">Done</th>
        </tr>
      </thead>
      <tbody>
        {rows.map((row) => (
          <tr key={row.key}>
            <th scope="row">{row.label}</th>
            <td>{row.planned}</td>
            <td>{row.completed}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
