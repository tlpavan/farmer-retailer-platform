/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#16241C',
        paper: '#F5F7F1',
        leaf: {
          50: '#EEF4EC',
          100: '#D7E6D2',
          300: '#7FAE79',
          500: '#2F6B3C',
          600: '#255630',
          700: '#1C4225',
        },
        gold: {
          100: '#F7E4C1',
          400: '#E3A73C',
          500: '#D98C2B',
          600: '#B5701D',
        },
        soil: {
          400: '#A67C52',
          500: '#8B5E34',
          600: '#6E4826',
        },
        brick: '#B33A3A',
      },
      fontFamily: {
        display: ['"Fraunces"', 'serif'],
        sans: ['"Inter"', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        soft: '0 1px 2px rgba(22,36,28,0.06), 0 8px 24px rgba(22,36,28,0.06)',
      },
    },
  },
  plugins: [],
}
