aws s3 cp ./target/function.zip s3://aminasif-code/lambda/wscomm-quk-lambda.zip  
aws lambda update-function-code --function-name wscomm-quk-lambda --s3-bucket aminasif-code  --s3-key lambda/wscomm-quk-lambda.zip --publish
echo "Done ..."