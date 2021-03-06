#!/usr/bin/env bash
#---------------------------------------------------------------------------------------------------
# Reboots one instance using the AWS CLI
#---------------------------------------------------------------------------------------------------

source $(dirname $0)/.check-requirements.sh

if [ -z "$1" ]; then
    echo "Usage: $(basename $0) instance-id-or-CNAME" >&2
    exit 1
fi

declare region=$(grep aws_region playbooks/vars/common.yml | cut -d: -f2)
declare AWS_EC2="aws --profile codekvast --region ${region} ec2"

case "$1" in
    i-*) instance=$1 ;;
    *) instance=$(${AWS_EC2} describe-instances --filter Name=tag:CNAME,Values=$1 | awk '/InstanceId/{print $2}' | tr -d '",');;
esac

echo -n "About to reboot ${instance}. Are you sure [N/y]: "; read answer
case "$answer" in
    ""|"N"|"n") exit 0;;
    "y"|"Y") echo "Ok, here we go...";;
esac

echo "Rebooting instance ${instance}..."
${AWS_EC2} reboot-instances --instance-ids ${instance}
