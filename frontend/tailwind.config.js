/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: "class",
  theme: {
    extend: {
      colors: {
        background: "#f8fafc", // Slate 50 clean canvas
        card: "#ffffff",       // Pure white cards
        border: "#e2e8f0",     // Slate 200 border
        primary: {
          DEFAULT: "#2563eb",  // Professional Blue 600
          hover: "#1d4ed8",    // Blue 700
          glowing: "rgba(37, 99, 235, 0.08)",
        },
        success: {
          DEFAULT: "#10b981",  // Emerald 500
          hover: "#059669",
          glowing: "rgba(16, 185, 129, 0.08)",
        },
        warning: {
          DEFAULT: "#f59e0b",  // Amber 500
          hover: "#d97706",
          glowing: "rgba(245, 158, 11, 0.08)",
        },
        danger: {
          DEFAULT: "#ef4444",  // Rose 500
          hover: "#dc2626",
          glowing: "rgba(239, 68, 68, 0.08)",
        },
        slate: {
          50: "#f8fafc",
          100: "#f1f5f9",
          200: "#e2e8f0",
          300: "#cbd5e1",
          400: "#94a3b8",
          500: "#64748b",
          600: "#475569",
          700: "#334155",
          850: "#1e293b", // mapped for backwards-compat with some inputs
          800: "#1e293b",
          900: "#0f172a",
          950: "#020617",
        }
      },
      fontFamily: {
        sans: ["Inter", "Outfit", "sans-serif"],
      },
      boxShadow: {
        glow: "0 0 15px -3px var(--tw-shadow-color)",
        card: "0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px -1px rgba(0, 0, 0, 0.05)",
      },
      animation: {
        "pulse-slow": "pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite",
        "spin-slow": "spin 8s linear infinite",
      }
    },
  },
  plugins: [],
}

