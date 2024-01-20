#!/bin/bash

# Display the menu
echo "Choose an option:"
echo "1. Update State Machine: SFuncExpress-AddTravelDocs.asl"
echo "2. Command 2"
echo "3. Update WEBSITE"
echo "4. Quit"

# Read user input
read -p "Enter your choice: " choice

# Execute the selected command
case $choice in
    1)
        echo "Executing Command 1"
        aws stepfunctions update-state-machine \
                --state-machine-arn "arn:aws:states:eu-west-1:094312144437:stateMachine:SFuncExpress-AddTravelDocs" \
                --definition "file:///Users/aminasif/amroot/work/AB3/stepfunctions/SFuncExpress-AddTravelDocs.asl.json"  
        ;;
    2)
        echo "Executing Command 2"
         # Add the command 2 here
        ;;
    3)
        echo "Executing Command 3"
        # Add the command 3 here
        
        aws s3 sync /Users/aminasif/amroot/work/code-projects/AB3/amin.guru/ s3://amin.guru --exclude "._*" --exclude "*/._*" --exclude ".DS_Store" --exclude "*/.DS_Store"
        ;;
    4)
        echo "Quitting..."
        exit 0
        ;;
    *)
        echo "Invalid choice"
        ;;
esac
