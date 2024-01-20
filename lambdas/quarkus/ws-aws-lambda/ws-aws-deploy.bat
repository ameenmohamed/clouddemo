aws s3 cp ./target/function.zip s3://aminasif-code/lambda/ws-aws-socketlambda.zip  
aws lambda update-function-code --function-name ws-aws-socketlambda --s3-bucket aminasif-code --s3-key lambda/ws-aws-socketlambda.zip 
echo "Done ..."