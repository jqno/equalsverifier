#!/bin/bash

# Determines the next version of EqualsVerifier.
# If the version was x.y, the new version will be x.y.1-SNAPSHOT
# If the version was x.y.z, the new version will be x.y.(z+1)-SNAPSHOT

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <version>"
    exit 1
fi

version="$1"
IFS='.' read -ra ADDR <<< "$version"

if [ ${#ADDR[@]} -eq 2 ]; then
    echo "$version.1-SNAPSHOT"
elif [ ${#ADDR[@]} -eq 3 ]; then
    (( ADDR[2]++ ))
    echo "${ADDR[0]}.${ADDR[1]}.${ADDR[2]}-SNAPSHOT"
else
    echo "Error: Version format not supported."
    exit 1
fi

