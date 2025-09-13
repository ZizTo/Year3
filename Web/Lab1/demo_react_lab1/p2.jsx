import React from "react"; 
import ReactDOM from "react-dom/client";

import FuncRegions from "./components/Regions";

const container = document.getElementById('reactapp');
const root = ReactDOM.createRoot(container);
root.render(<FuncRegions />);