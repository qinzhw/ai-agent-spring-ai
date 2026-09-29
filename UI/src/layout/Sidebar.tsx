import React from "react";

interface SidebarProps {
  children: React.ReactNode;
}

const Sidebar: React.FC<SidebarProps> = ({ children }) => {
  return (
    <div
      className="h-full shrink-0 bg-slate-50"
      style={{
        width: "320px",
      }}
    >
      {children}
    </div>
  );
};

export default Sidebar;
