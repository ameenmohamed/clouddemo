# AWS CLI TO copy file from target folder to s3 location as lambdafunctioname.zip
aws s3 cp ./target/function.zip s3://aminasif-code/lambda/CDWSPUploadFileLambda.zip 


# AWS CLI TO publish the function zip  from s3 bucket to lambda
aws lambda update-function-code --function-name CDWSPUploadFileLambda --s3-bucket aminasif-code --s3-key lambda/CDWSPUploadFileLambda.zip --publish