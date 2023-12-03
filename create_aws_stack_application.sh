
function wait_for_stack_creation {
  echo "Please Wait "
  STACK_STATUS="CREATE_IN_PROGRESS"

  while [[ $STACK_STATUS = "CREATE_IN_PROGRESS" ]]
  do
    sleep 3
    STACK_STATUS=$(aws cloudformation describe-stacks --query "Stacks[?StackName==\`$1\`].StackStatus" --output text)
    echo -n "."
  done

  echo ""

  if [[ $STACK_STATUS != "CREATE_COMPLETE" ]]
  then
    echo "The stack $1 creation Failed. Please access the AWS Management Console. Status: $STACK_STATUS"
    exit 125
  fi

    echo "Stack $1 successfully created."

}

aws --region  us-east-1 cloudformation create-stack --stack-name wex-purchase-tx --template-body file://cloudformation.yaml --capabilities CAPABILITY_NAMED_IAM
wait_for_stack_creation " wex-purchase-tx"
