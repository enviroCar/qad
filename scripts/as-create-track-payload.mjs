#!/usr/bin/env -S node --experimental-modules

import { promises as fs } from "fs";
import { fileURLToPath } from "url";
import { dirname, resolve } from "path";

const propertiesToDelete = ["id", "length", "begin", "end"];

function modifyTrack(track) {
  for (let name of propertiesToDelete) {
    delete track.properties[name];
  }
  track.properties.sensor = track.properties.sensor.properties.id;

  for (let feature of track.features) {
    for (let name of ["id"]) {
      delete feature.properties[name];
    }
    for (let key of Object.keys(feature.properties.phenomenons)) {
      delete feature.properties.phenomenons[key].unit;
      //feature.properties.phenomenons[key] =
      //  feature.properties.phenomenons[key].value;
    }
  }
  return track;
}

async function processFile(path, fileName) {
  const content = await fs.readFile(resolve(path, fileName));
  const track = JSON.parse(content);
  const id = track.properties.id;
  const data = modifyTrack(track);
  const newFileName = resolve(path, `${id}.insert.json`);
  await fs.writeFile(newFileName, JSON.stringify(data, null, 2));
}

async function main(path) {
  const fileNames = await fs.readdir(path);
  await Promise.all(fileNames.map(file => processFile(path, file)));
}

(async () => {
  try {
    const directory = resolve(
      dirname(fileURLToPath(import.meta.url)), "tracks"
    );
    console.log(await main(directory));
  } catch (e) {
    console.error(e);
  }
})();
