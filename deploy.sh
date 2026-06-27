#!/usr/bin/env bash
set -e

cd /opt/gmrules

if [ -f /opt/gmrules/.env ]; then
  echo "Loading environment..."
  set -a
  . /opt/gmrules/.env
  set +a
fi

echo "Pulling latest code..."
git pull

echo "Compiling..."
mvn -q -DskipTests compile

echo "Stopping old app..."
pkill -f 'com.gamemaker.gmrules.web.WebMain' || true

sleep 3

echo "Starting app..."
nohup mvn -pl gmrules-builder exec:java -Dexec.mainClass=com.gamemaker.gmrules.web.WebMain > /opt/gmrules/gmrules.log 2>&1 &

sleep 3

echo "Current Java process:"
ps aux | grep '[j]ava' || true

echo "Recent app log:"
tail -40 /opt/gmrules/gmrules.log || true
