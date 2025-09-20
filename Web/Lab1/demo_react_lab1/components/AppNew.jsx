import React  from "react";
import ReactDOM from "react-dom";
import { useState } from "react"; 
import { HashRouter, BrowserRouter as Router, Route, Switch, Link } from "react-router-dom";
import FuncExample from './Example';
import FuncRegions from './Regions';
import FuncAvto from './Avto';
import Grid from "@mui/material/Grid";

function AppNew() {
    return (
    <HashRouter> 
      <div>
        <Grid container component="ul" spacing={3}>
          <Grid item component="li">
            <Link to="/example">Example</Link>
          </Grid>
          <Grid item component="li">
            <Link to="/regions">Regions</Link>
          </Grid>
          <Grid item component="li">
            <Link to="/avto">Avto</Link>
          </Grid>
        </Grid>

        <hr />

        {/*
          A <Switch> looks through all its children <Route>
          elements and renders the first one whose path
          matches the current URL. Use a <Switch> any time
          you have multiple routes, but you want only one
          of them to render at a time
        */}
        <Switch>
          <Route exact path="/example">
            <FuncExample />
          </Route>
          <Route path="/regions">
            <FuncRegions />
          </Route>
          <Route path="/avto">
            <FuncAvto />
          </Route>
        </Switch>
      </div>
    </HashRouter>
  );
}

export default AppNew;