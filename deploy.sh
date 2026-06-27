#!/usr/bin/env bash
set -euo pipefail

cd /opt/gmrules

echo "Pulling latest code..."
git pull

echo "Compiling..."
mvn -q -DskipTests compile

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
