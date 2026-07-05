#!/usr/bin/env bash
set -euo pipefail

cd /opt/gmrules

echo "Pulling latest code..."
git pull

echo "Compiling..."
mvn -q -pl gmrules-builder -am -DskipTests clean compile

echo "Verifying compiled API routes..."
javap -classpath gmrules-builder/target/classes:gmrules-core/target/classes -verbose com.gamemaker.gmrules.web.ApiRoutes \
  | grep -q '/api/characters/import'
javap -classpath gmrules-builder/target/classes:gmrules-core/target/classes -verbose com.gamemaker.gmrules.web.ApiRoutes \
  | grep -q '/api/characters/export'

echo "Restarting gmrules.service..."
systemctl restart gmrules

sleep 5

echo "Service status:"
systemctl status gmrules --no-pager

echo
echo "Java process:"
ps aux | grep '[j]ava' || true

echo
echo "Recent service log:"
journalctl -u gmrules -n 40 --no-pager
