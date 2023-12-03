function wait_for_stack_exclusion {
  echo "Please Wait "
  STACK_STATUS="DELETE_IN_PROGRESS"

  while [[ $STACK_STATUS = "DELETE_IN_PROGRESS" ]]
  do
    sleep 3
    STACK_STATUS=$(aws cloudformation list-stacks --query "reverse(sort_by(StackSummaries[?StackName=='$1'], &CreationTime))[0].StackStatus" | tr -d '"')
    echo -n "."
  done

  echo ""

  if [[ $STACK_STATUS != "DELETE_COMPLETE" ]]
  then
    echo "The stack $1 exclusion Failed. Please access the AWS Management Console. Status: $STACK_STATUS"
    exit 125
  fi

  echo "Stack $1 successfully deleted."
}

aws cloudformation delete-stack --stack-name wex-purchase-tx
wait_for_stack_exclusion "wex-purchase-tx"