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
        background: "#0b0f19",
        card: "#131c2e",
        border: "#1e293b",
        primary: {
          DEFAULT: "#3b82f6",
          hover: "#2563eb",
          glowing: "rgba(59, 130, 246, 0.15)",
        },
        success: {
          DEFAULT: "#10b981",
          hover: "#059669",
          glowing: "rgba(16, 185, 129, 0.15)",
        },
        warning: {
          DEFAULT: "#f59e0b",
          hover: "#d97706",
          glowing: "rgba(245, 158, 11, 0.15)",
        },
        danger: {
          DEFAULT: "#ef4444",
          hover: "#dc2626",
          glowing: "rgba(239, 68, 68, 0.15)",
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
          800: "#1e293b",
          900: "#0f172a",
          950: "#020617",
        }
      },
      fontFamily: {
        sans: ["Outfit", "Inter", "sans-serif"],
      },
      boxShadow: {
        glow: "0 0 15px -3px var(--tw-shadow-color)",
        card: "0 4px 20px -2px rgba(0, 0, 0, 0.3)",
      },
      animation: {
        "pulse-slow": "pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite",
        "spin-slow": "spin 8s linear infinite",
      }
    },
  },
  plugins: [],
}
