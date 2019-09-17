#!/bin/node

"use strict";
var fs = require("fs");
var args = process.argv.slice(2);
var input = args[0];

if (args.length < 1) {
    throw new Error("missing input");
}

fs.readFile(input, function (err, content) {
    if (err) throw err;
    var data = JSON.parse(content);
    content = JSON.stringify(preprocess(data), null, 2);
    fs.writeFile(input, content, function (err) {
        if (err) throw err;
    });
});

var propertiesToDelete = [
    "Shape_Len",
    "Shape_Length",
    "zul_V",
    "Segment_Name",
    "Segment_Na",
    "Segmenttyp",
    "Achsen_ID"
];

function preprocess(data) {
    data.features.forEach(function (feature) {
        var p = feature.properties;
        if (p.Achsen_ID) {
            p.axis = parseInt(p.Achsen_ID.split("_")[0]);
            p.direction = parseInt(p.Achsen_ID.split("_")[1]);
        }
        if (p.rank) {
            p.rank = parseInt(p.rank);
        }
        if (p.zul_V) {
            p.maxSpeed = parseInt(p.zul_V);
        }
        if (p.maxSpeed) {
            p.maxSpeed = parseInt(p.maxSpeed);
        }
        if (p.Segmenttyp) {
            p.type = parseInt(p.Segmenttyp);
        }
        if (p.Segment_Name) {
            p.name = p.Segment_Name;
        }
        if (p.Segment_Na) {
            p.name = p.Segment_Na;
        }
        propertiesToDelete.forEach(function (name) {
            delete p[name];
        })
        ;
    });

    return data;
}
