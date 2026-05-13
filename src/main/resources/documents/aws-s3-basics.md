# AWS S3 Basics

Amazon S3 (Simple Storage Service) is an object storage service.
- **Buckets**: S3 stores data in containers called buckets. A bucket name must be globally unique.
- **Objects**: Objects are the fundamental entities stored in S3. They consist of data and metadata.
- **Storage Classes**: S3 offers various storage classes for different use cases, such as S3 Standard for frequently accessed data and S3 Glacier for long-term archive.
- **CLI Command**: To list all buckets, use the command: `aws s3 ls`