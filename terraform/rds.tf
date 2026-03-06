# RDS PostgreSQL Database

# -----------------------------------------------------------------------------
# DB Subnet Group
# -----------------------------------------------------------------------------

resource "aws_db_subnet_group" "main" {
  name       = "${var.project_name}-${var.environment}-db-subnet"
  subnet_ids = data.aws_subnets.default.ids

  tags = {
    Name = "${var.project_name}-${var.environment}-db-subnet"
  }
}

# -----------------------------------------------------------------------------
# DB Parameter Group
# -----------------------------------------------------------------------------

resource "aws_db_parameter_group" "postgres" {
  name_prefix = "${var.project_name}-${var.environment}-pg"
  family      = "postgres14"

  parameter {
    name  = "log_connections"
    value = "1"
  }

  lifecycle {
    create_before_destroy = true
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-postgres-params"
  }
}

# -----------------------------------------------------------------------------
# RDS PostgreSQL Instance
# -----------------------------------------------------------------------------

resource "aws_db_instance" "postgres" {
  identifier     = "${var.project_name}-${var.environment}-db"
  engine         = "postgres"
  engine_version = var.db_engine_version
  instance_class = var.db_instance_class

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password
  port     = 5432

  allocated_storage     = var.db_allocated_storage
  storage_type          = "gp3"
  storage_encrypted     = true
  max_allocated_storage = 100

  backup_retention_period  = var.db_backup_retention_days
  backup_window            = "03:00-04:00"
  maintenance_window       = "mon:04:00-mon:05:00"
  delete_automated_backups = true

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  publicly_accessible    = false

  parameter_group_name = aws_db_parameter_group.postgres.name

  enabled_cloudwatch_logs_exports = ["postgresql"]

  deletion_protection = false
  skip_final_snapshot = true
  apply_immediately   = true

  tags = {
    Name = "${var.project_name}-${var.environment}-postgres"
  }
}
