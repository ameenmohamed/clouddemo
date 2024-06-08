import json
import boto3

def lambda_handler(event, context):

// List all items in an s3 bucket called abc123
    s3 = boto3.resource('s3')
    bucket = s3.Bucket('XXXXXX')
    for obj in bucket.objects.all():
        print(obj.key)
  
    return {
        'statusCode': 200,
        'body': json.dumps(event)
    }
