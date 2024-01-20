aws s3 cp ./target/function.zip s3://aminasif-code/lambda/sqspoll-quk-lambda.zip   
aws lambda update-function-code --function-name sqspoll-quk --s3-bucket aminasif-code --s3-key lambda/sqspoll-quk-lambda.zip  --publish
echo "Done ..."