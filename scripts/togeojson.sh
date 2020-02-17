

set -ex

FGDB="$1"
LAYER=$(ogrinfo "${FGDB}" | grep -Po 'Achsensegmente_[0-9]+')
GEOJSON="$(basename "${FGDB}" .gdb).geojson"
rm -f "${GEOJSON}"
ogr2ogr -f GeoJSON -t_srs crs:84 "${GEOJSON}" "${FGDB}" "${LAYER}"

