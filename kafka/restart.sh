#!/bin/sh

cd "$(dirname "$0")"

sudo docker-compose down

sudo docker run --rm -i \
  -v "$PWD/volumes:/volumes" \
  busybox:1.31.0 \
  find /volumes/ -maxdepth 1 -mindepth 1 -exec rm -rf {} \;

sudo docker-compose up -d
