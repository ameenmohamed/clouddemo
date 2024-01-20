aws s3 cp ./target/function.zip s3://aminasif-code/lambda/login-quk-lambda.zip  
aws lambda update-function-code --function-name login-quk --s3-bucket aminasif-code --s3-key lambda/login-quk-lambda.zip --publish
echo "Done ..."