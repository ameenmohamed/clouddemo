
# aws cli to update lambda fnction with change in handler , iam role and memory size
aws lambda update-function-configuration --function-name CDWSPUploadFileLambda \
    --handler "org.hac.codewsp.lambdas.lambda.UploadFileLambda::handleRequest" \
    --role "arn:aws:iam::077772100037:role/Role-BaseLambda" \
    --memory-size  512  
