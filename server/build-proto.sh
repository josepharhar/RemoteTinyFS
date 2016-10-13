mkdir generated-src -p
rm -rf generated-src/*
protoc -I protobuf --java_out=generated-src protobuf/*.proto
