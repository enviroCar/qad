#!/bin/node --experimental-modules

import {readFile, writeFile} from "fs";

const args = process.argv.slice(2);

if (args.length < 1) {
    throw new Error("missing input");
}
args.forEach(input => {
    readFile(input, (err, content) => {
        if (err) throw err;
        const data = JSON.parse(content);
        content = JSON.stringify(preprocess(data), null, 2);
        writeFile(input, content, err => {
            if (err) throw err;
        });
    });
});

const propertiesToDelete = [
    "Shape_Len",
    "Shape_Length",
    "zul_V",
    "Segment_Name",
    "Segment_Na",
    "Segmenttyp",
    "Achsen_ID",
    "name",
    "id"
];

function preprocess(data) {
    data.features.forEach(feature => {
        const p = feature.properties;
        if (p["Achsen_ID"] !== undefined) {
            const s = p["Achsen_ID"].split("_");
            p.axis = parseInt(s[0]);
            p.direction = parseInt(s[1]);
        }
        if (p.rank !== undefined) {
            p.rank = parseInt(p.rank);
        }
        if (p["zul_V"] !== undefined) {
            p.maxSpeed = parseInt(p["zul_V"]);
        }
        if (p.maxSpeed !== undefined) {
            p.maxSpeed = parseInt(p.maxSpeed);
        }
        if (p["Segmenttyp"] !== undefined) {
            p.type = parseInt(p["Segmenttyp"]);
        }
        propertiesToDelete.forEach(function (name) {
            delete p[name];
        });
    });

    data.features.sort((f1, f2) => {
        const p1 = f1.properties;
        const p2 = f2.properties;
        if (p1.axis < p2.axis) return -1;
        if (p1.axis > p2.axis) return 1;
        if (p1.direction < p2.direction) return -1;
        if (p1.direction > p2.direction) return 1;
        if (p1.rank < p2.rank) return -1;
        if (p1.rank > p2.rank) return 1;
        return 0
    });

    return data;
}
