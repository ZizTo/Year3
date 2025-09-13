import React, { useState } from "react";
import "./styles.css";

/**
 * Define Regions, a React component of lab1. The model
 * data for this view (the regions names) is available at
 * window.regionsModel.regionsModel().
 */

function FuncRegions(props) {
  const [substr, setSubstr] = useState("");
  const [regionsFilt, setRegionsFilt] = useState("");

  const handleChangeSubstr = (event) => {
    var matchingElements = "";
    window.lab1models.regionsModel().forEach(element => {
        if (element.toLowerCase().includes(event.target.value.toLowerCase())) {
          matchingElements += element + "\n";
        }
      });
    setSubstr(event.target.value);
    setRegionsFilt(matchingElements);
  };

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
          <div>
            {regionsFilt}
          </div> 
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
export default FuncRegions;
