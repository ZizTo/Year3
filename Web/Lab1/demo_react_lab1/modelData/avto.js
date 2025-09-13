"use strict";

/*
 * Load the model data for lab1. We load into the property
 * lab1models.regionsModel a function that returns an array of strings with the
 * names of the regions.
 *
 * 
 */

var lab1models;

if (lab1models === undefined) {
  lab1models = {};
}

lab1models.avtoModel = function () {
  return [
    "BMW",
    "Toyota",
    "KIA",
    "Lambo",
    "Mitsubishi",
    "Povozka",
    "mErSedess",
    "RedBull",    
  ];
};
