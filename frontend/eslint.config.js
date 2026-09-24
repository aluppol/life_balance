import js from '@eslint/js';
import { defineConfig, globalIgnores } from 'eslint/config';
import jsxA11y from 'eslint-plugin-jsx-a11y';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import globals from 'globals';
import tseslint from 'typescript-eslint';

const noComments = {
  meta: {
    type: 'suggestion',
    schema: [],
    messages: { comment: 'Comments are not allowed: let names and types explain the code.' },
  },
  create(context) {
    return {
      Program() {
        for (const comment of context.sourceCode.getAllComments()) {
          context.report({ loc: comment.loc, messageId: 'comment' });
        }
      },
    };
  },
};

const noDefaultExports = {
  restrictDefaultExports: {
    direct: true,
    named: true,
    defaultFrom: true,
    namedFrom: true,
    namespaceFrom: true,
  },
};

export default defineConfig([
  globalIgnores(['dist', 'coverage', 'reports', '.stryker-tmp', '.gradle', 'build']),
  {
    plugins: { standards: { rules: { 'no-comments': noComments } } },
    rules: {
      'standards/no-comments': 'error',
      'max-lines-per-function': ['error', { max: 30, skipBlankLines: true, skipComments: true }],
    },
  },
  {
    files: ['**/*.{js,mjs}'],
    extends: [js.configs.recommended],
    languageOptions: { globals: globals.node },
  },
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      js.configs.recommended,
      tseslint.configs.strictTypeChecked,
      tseslint.configs.stylisticTypeChecked,
    ],
    languageOptions: {
      parserOptions: { projectService: true, tsconfigRootDir: import.meta.dirname },
    },
    rules: {
      '@typescript-eslint/no-confusing-void-expression': ['error', { ignoreArrowShorthand: true }],
    },
  },
  {
    files: ['src/**/*.{ts,tsx}'],
    extends: [
      reactHooks.configs.flat['recommended-latest'],
      reactRefresh.configs.vite,
      jsxA11y.flatConfigs.strict,
    ],
    languageOptions: { globals: globals.browser },
  },
  {
    files: ['src/**/*.{ts,tsx}', 'dev/**/*.{ts,mjs}'],
    rules: { 'no-restricted-exports': ['error', noDefaultExports] },
  },
]);
