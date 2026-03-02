terraform {
  # Local backend for initial development
  # Will migrate to Azure Storage backend before production deployment
  backend "local" {
    path = "terraform.tfstate"
  }
}
