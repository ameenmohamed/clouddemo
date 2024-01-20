# Variable names
FUNCTION_NAME="img-metadata-quarkus"
FUNCTION_CLASS="S3LambdaHandler"
FUNCTION_METHOD="handleRequest"
FUNCTION_PACKAGE="org.hac.lambda"
FUNCTION_MEM=256

aws lambda update-function-configuration --function-name $FUNCTION_NAME \
    --handler "$FUNCTION_PACKAGE.$FUNCTION_CLASS::$FUNCTION_METHOD" \
    --role "arn:aws:iam::094312144437:role/Role-BaseLambda" \
    --memory-size  $FUNCTION_MEM  