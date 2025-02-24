
# Variable names
FUNCTION_NAME="img-metadata-quarkus"
S3_BUCKET="aminasif-code"
S3_KEY="lambda/$FUNCTION_NAME.zip"

# Upload function code to S3
aws s3 cp ./target/function.zip s3://$S3_BUCKET/$S3_KEY

# Update Lambda function code
aws lambda update-function-code --function-name $FUNCTION_NAME --s3-bucket $S3_BUCKET --s3-key $S3_KEY --publish

echo "Done ..."