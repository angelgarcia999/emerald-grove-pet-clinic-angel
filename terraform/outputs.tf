output "app_url" {
  value = "http://${aws_lb.main.dns_name}"
}

output "db_endpoint" {
  value = aws_db_instance.postgres.endpoint
}
