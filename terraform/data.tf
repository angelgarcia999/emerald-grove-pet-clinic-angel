# Data sources for AWS resources

# Get default VPC
data "aws_vpc" "default" {
  default = true
}

# Get subnets in the default VPC
data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# Get availability zones
data "aws_availability_zones" "available" {
  state = "available"
}
