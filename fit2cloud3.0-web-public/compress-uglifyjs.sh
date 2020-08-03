#!/bin/sh

# 压缩所有第三方JS
FILE=compress/external

JS=$(cat ${FILE})
ls -l

cat ${JS} | uglifyjs --compress --keep-fnames --output src/main/resources/web-public/external/external.min.js
