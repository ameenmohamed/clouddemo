#!/bin/bash
counter=0
distributionId=ELLMZVK838JMA
# Ask the user for input
echo "Enter the paths to invalidate on Cloudfront:"
read inpath

# Use the user input in a command execution
echo "Executing cloundfront invlidateation command for path: '$inpath'"

invalidation_id=$(aws cloudfront create-invalidation --distribution-id "$distributionId" --paths "$inpath" --query 'Invalidation.Id' --output text)
# watch -n 5 aws cloudfront get-invalidation --distribution-id ELLMZVK838JMA --id $invalidation_id
# watch -n 1 'response=$(aws cloudfront get-invalidation --distribution-id ELLMZVK838JMA --id '$invalidation_id'); status=$(echo "$response" | jq -r ".Invalidation.Status"); echo "Current status: $status"; if [[ "$status" == "Completed" ]]; then echo "Invalidation completed. Stopping watch."; exit 0; fi'

watch -n 1 '
  echo "Command count: $((++counter))"
  response=$(aws cloudfront get-invalidation --distribution-id '$distributionId' --id '$invalidation_id')
  status=$(echo "$response" | jq -r ".Invalidation.Status")
  
 
  echo "Current status: $status"
  
  if [[ "$status" == "Completed" ]]; then
    echo "Invalidation completed. Stopping watch."
    pkill -P $$ watch
  fi
'
