aws lambda update-function-configuration --function-name wscomm-quk-lambda \
    --handler "org.anycompany.hac.lambda.WSCommsStreamLambda::handleRequest" \
    --role "arn:aws:iam::094312144437:role/Role-BaseLambda" \
    --memory-size  256  