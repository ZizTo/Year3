import React from "react"; 
import ReactDOM from "react-dom/client";
import "./styles/main.css";

import FuncExample from "./components/Example";

const container = document.getElementById('reactapp');
const root = ReactDOM.createRoot(container);
root.render(<FuncExample />);
