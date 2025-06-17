import React from "react";
import ThemeToggle from "./ThemeToggle";

export default function AppHeader() {
  return (
    <header className="fixed top-0 left-0 w-full flex justify-end p-4 z-50 pointer-events-none">
      <div className="pointer-events-auto">
        <ThemeToggle />
      </div>
    </header>
  );
}