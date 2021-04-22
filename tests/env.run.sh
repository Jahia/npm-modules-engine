#!/bin/bash
# This script can be used to warmup the environment and execute the tests
# It is used by the docker image at startup

if [[ ! -f .env ]]; then
 cp .env.example .env
fi

source .env

# Execute expansions to have file understandable by docker-compose and node dotenv
mv .env .env.orig
( cat .env.orig ; echo "cat <<EOF" ; cat .env.orig ; echo EOF ) | sh > .env

#!/usr/bin/env bash
START_TIME=$SECONDS

echo " == Using MANIFEST: ${MANIFEST}"
echo " == Using JAHIA_URL= ${JAHIA_URL}"

echo " == Waiting for Jahia to startup"
while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' ${JAHIA_URL}/cms/login)" != "200" ]];
  do sleep 5;
done
ELAPSED_TIME=$(($SECONDS - $START_TIME))
echo " == Jahia became alive in ${ELAPSED_TIME} seconds"

mkdir -p ./run-artifacts
mkdir -p ./results

# Add the credentials to a temporary manifest for downloading files
# Execute jobs listed in the manifest
# If the file doesn't exist, we assume it is a URL and we download it locally
if [[ -e ${MANIFEST} ]]; then
  cp ${MANIFEST} ./run-artifacts
else
  echo "Downloading: ${MANIFEST}"
  curl ${MANIFEST} --output ./run-artifacts/curl-manifest
  MANIFEST="curl-manifest"
fi
sed -i "" -e "s/NEXUS_USERNAME/${NEXUS_USERNAME}/g" ./run-artifacts/${MANIFEST}
sed -i "" -e "s/NEXUS_PASSWORD/${NEXUS_PASSWORD}/g" ./run-artifacts/${MANIFEST}
sed -i "" -e "s/JAHIA_VERSION/${JAHIA_VERSION}/g" ./run-artifacts/${MANIFEST}

echo JAHIA_URL=${JAHIA_URL} > ./run-artifacts/cypress.env
echo SUPER_USER_PASSWORD=${SUPER_USER_PASSWORD} >> ./run-artifacts/cypress.env

echo "$(date +'%d %B %Y - %k:%M') == Warming up the environement =="
curl -u root:${SUPER_USER_PASSWORD} -X POST ${JAHIA_URL}/modules/api/provisioning --form script="@./run-artifacts/${MANIFEST};type=text/yaml"
echo "$(date +'%d %B %Y - %k:%M') == Environment warmup complete =="

# If we're building the module (and manifest name contains build), then we'll end up pushing that module individually
for file in ./artifacts/*-SNAPSHOT.jar
do
  echo "$(date +'%d %B %Y - %k:%M') == Submitting module from: $file =="
  curl -s --user root:${SUPER_USER_PASSWORD} --form bundle=@$file --form start=true ${JAHIA_URL}/modules/api/bundles
  echo "$(date +'%d %B %Y - %k:%M') == Module submitted =="
done

echo "$(date +'%d %B %Y - %k:%M')== Run tests =="
yarn e2e:ci
if [[ $? -eq 0 ]]; then
  echo "success" > ./results/test_success
  exit 0
else
  echo "failure" > ./results/test_failure
  exit 1
fi

# After the test ran, we're dropping a marker file to indicate if the test failed or succeeded based on the script test command exit code
