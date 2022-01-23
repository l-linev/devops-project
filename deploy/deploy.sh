#!/bin/bash
set -e
# shellcheck disable=SC2034
__dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && printf "%q\n" "$(pwd)")"
__raw_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && printf "$(pwd)")"
pushd "$__raw_dir"
popd

VERSION=${1:-latest}
# identifier for custom deployments like PR deployments
# leave empty for main deployment release

# used to force resource recreation
HASH=${HASH:-$(date '+%s')}

CLUSTER_NAME="project-cluster"
SERVICE_NAME="devops-project"
STACK_NAME="ecs-${CLUSTER_NAME}-${SERVICE_NAME}"
IMAGE="914194858346.dkr.ecr.us-east-1.amazonaws.com/devops_project:$VERSION"
DESIRED_COUNT=1

# CPU | (Memory)
# 256 (.25 vCPU) | 512 (0.5GB), 1024 (1GB), 2048 (2GB)
# 512 (.5 vCPU)  | 1024 (1GB), 2048 (2GB), 3072 (3GB), 4096 (4GB)
# 1024 (1 vCPU)  | 2048 (2GB), 3072 (3GB), 4096 (4GB), 5120 (5GB), 6144 (6GB), 7168 (7GB), 8192 (8GB)
# 2048 (2 vCPU)  | Between 4096 (4GB) and 16384 (16GB) in increments of 1024 (1GB)
# 4096 (4 vCPU)  | Between 8192 (8GB) and 30720 (30GB) in increments of 1024 (1GB)
CPU_TOTAL=256
MEMORY_TOTAL=512
MEMORY_BACKEND=512

LAUNCH_TYPE="FARGATE"
REGION="us-east-1"
SUBNETS="subnet-07b2188fa854b9572,subnet-0e57291b038ff250a"

aws iam create-service-linked-role --aws-service-name ecs.amazonaws.com

aws cloudformation deploy \
--template-file "${__raw_dir}/stack.yaml" \
--stack-name "$STACK_NAME" \
--parameter-overrides \
    ClusterName="$CLUSTER_NAME" \
    ServiceName="$SERVICE_NAME" \
    ImageUrl="$IMAGE" \
    LaunchType="$LAUNCH_TYPE" \
    Subnets="$SUBNETS" \
    DesiredCount="$DESIRED_COUNT" \
    ContainerMemoryTotal="$MEMORY_TOTAL" \
    ContainerMemoryBackend="$MEMORY_BACKEND" \
    ContainerCpuTotal="$CPU_TOTAL" \
    Hash="$HASH" \
--no-fail-on-empty-changeset \
--capabilities CAPABILITY_IAM \
--tags \
    service_host="ECS" \
    cluster_name="$CLUSTER_NAME" \
    service_name="$SERVICE_NAME" \
--region "$REGION"

if [[ $(aws cloudformation describe-stacks --stack-name "$STACK_NAME" --region "$REGION" | jq -r '.Stacks[0].StackStatus') =~ UPDATE_COMPLETE|CREATE_COMPLETE ]]; then
    cleanup_failed_change_sets "${STACK_NAME}" "${REGION}"
    exit 0
fi
aws cloudformation describe-stacks --stack-name "$STACK_NAME" --region "$REGION"
exit 1
