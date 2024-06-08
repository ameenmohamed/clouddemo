# AWS CLI TO copy FUNCTION.ZIP FROM target folder to s3 location as lambdafunctioname.zip
aws s3 cp ./target/function.zip s3://amroot-work/lambda/CDWSPUploadFileLambda.zip  --profile hac


# AWS CLI TO publish the function zip  from s3 bucket use profile hac
aws lambda update-function-code --function-name CDWSPUploadFileLambda \
 --s3-bucket amroot-work --s3-key lambda/CDWSPUploadFileLambda.zip  --publish --profile hac




