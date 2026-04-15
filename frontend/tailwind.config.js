/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        ink: "#102030",
        mist: "#edf3f8",
        accent: "#ff7a59",
        sea: "#5cb8a5",
        steel: "#4f5d75",
      },
      fontFamily: {
        display: ["Poppins", "sans-serif"],
        body: ["Manrope", "sans-serif"],
      },
      boxShadow: {
        panel: "0 20px 60px rgba(16, 32, 48, 0.10)",
      },
    },
  },
  plugins: [],
};
