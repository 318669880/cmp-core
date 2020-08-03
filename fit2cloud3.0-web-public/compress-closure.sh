#!/bin/sh

# 使用Google Closure Compiler压缩

echo "copy fit2cloud-common.js to compress"
cp src/main/resources/web-public/fit2cloud/js/fit2cloud-common.js compress/

echo "start compress fit2cloud-common.js"

cd compress

java -jar compiler.jar \
    --compilation_level WHITESPACE_ONLY \
    --js fit2cloud-common.js \
    --js_output_file fit2cloud-common.min.js \
    --create_source_map fit2cloud-common.min.js.map \
    --output_wrapper "%output%//# sourceMappingURL=fit2cloud-common.min.js.map"

cd ..

rm -rf compress/fit2cloud-common.js
mv compress/fit2cloud-common.min.js.map src/main/resources/web-public/fit2cloud/js/
mv compress/fit2cloud-common.min.js src/main/resources/web-public/fit2cloud/js/

echo "compress success"

