import React, { useState } from "react";
import "./styles.css";

import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
//import ListItemText from '@mui/material/ListItemText';
/**
 * Define Regions, a React component of lab1. The model
 * data for this view (the regions names) is available at
 * window.regionsModel.regionsModel().
 */

function FuncAvto(props) {
  const [substr, setSubstr] = useState("");
  const [avtoFilt, setAvtoFilt] = useState([]);

  const handleChangeSubstr = (event) => {
    var matchingElements = [];
    window.lab1models.avtoModel().forEach(element => {
        if (element.toLowerCase().includes(event.target.value.toLowerCase())) {
          matchingElements.push(element);
        }
      });
    setSubstr(event.target.value);
    setAvtoFilt(matchingElements);
  };

  var listitems = [];
  for (let i = 0; i < avtoFilt.length; i++) {
    listitems.push(<ListItem key={i}>{avtoFilt[i]}</ListItem>);
  }
  listitems = (listitems.length == 0) ? [<ListItem key={0}>Ничего не найдено</ListItem>] : listitems;

  return (
	<div>
           <div className="state-search">
            {			  
            substr
            }
           </div>
           
           <div className="lab1-example-output"><span id='IInfo'></span></div> 
          <label htmlFor="substrId">Enter substring to search:</label>
          <input
            id="substrId"
            type="text"
            value={substr}
            onChange={handleChangeSubstr}
          />
          <List>
            {listitems}
          </List>
    </div>
    );
}

/*class Regions extends React.Component {
  constructor(props) {
    super(props);
    console.log(
      "window.lab1models.regionsModel()",
      window.lab1models.regionsModel()
    );

    this.state={substr : "",};

    
    this.handleChangeSubstr = (event) => this.handleChange(event);
  }


  
  handleChange(event) {
    var matchingElements = "";
    window.lab1models.regionsModel().forEach(element => {
        if (element.toLowerCase().includes(event.target.value.toLowerCase())) {
          matchingElements += element + "\n";
        }
      });
    this.setState({subArray: matchingElements, substr: event.target.value});
  } 

  render() {
    return (
	<div>
           <div className="state-search">
            {			  
            this.state.substr
            }
           </div>
           
           <div className="lab1-example-output"><span id='IInfo'></span></div> 
          <label htmlFor="substrId">Enter substring to search:</label>
          <input
            id="substrId"
            type="text"
            value={this.state.substr}
            onChange={this.handleChangeSubstr}
          />
          <div>
            {this.state.subArray}
          </div> 
    </div>
    );
  }
}
*/
export default FuncAvto;
