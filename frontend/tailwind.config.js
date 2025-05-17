module.exports = {
  darkMode: 'class',
  content: [
    "./index.html",
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'healthcare-blue': '#1a365d',
        'medical-teal': '#2c7a7b',
        'emergency-red': '#c53030',
        'healthcare-light-blue': '#e6f2ff',
        'healthcare-dark-teal': '#235b5b'
      },
      gridTemplateColumns: {
        'registration': 'repeat(2, minmax(0, 1fr))',
      },
      boxShadow: {
        'medical': '0 4px 14px 0 rgba(0, 118, 255, 0.1)',
        'card-hover': '0 8px 25px -5px rgba(0, 0, 0, 0.1)',
        'input-focus': '0 0 0 3px rgba(26, 54, 93, 0.2)',
        'button-hover': '0 4px 6px -1px rgba(26, 54, 93, 0.2)'
      },
      keyframes: {
        pulse: {
          '0%, 100%': { opacity: '1' },
          '50%': { opacity: '0.5' }
        },
        spin: {
          '0%': { transform: 'rotate(0deg)' },
          '100%': { transform: 'rotate(360deg)' }
        }
      },
      animation: {
        pulse: 'pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        spin: 'spin 1s linear infinite'
      },
      backgroundImage: {
        'healthcare-gradient': 'linear-gradient(to bottom right, #e6f2ff, #f0fdf4)',
        'login-gradient': 'linear-gradient(to bottom right, #f0fdfa, #ecfdf5)'
      },
      borderRadius: {
        'xl': '1rem',
        '2xl': '1.5rem'
      }
    },
  },
  plugins: [
    require('@tailwindcss/forms')({
      strategy: 'class', // only generate classes
    }),
  ],
}