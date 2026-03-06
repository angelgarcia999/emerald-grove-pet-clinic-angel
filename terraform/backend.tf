terraform {
  # Local backend for development
  # TODO: Migrate to Azure Storage backend when setting up GitHub Actions (Issue #007)
  backend "local" {
    path = "terraform.tfstate"
  }
}
