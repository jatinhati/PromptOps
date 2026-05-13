
# AWS CLI – Getting Started

The **AWS Command Line Interface (CLI)** is a unified tool to manage AWS services.  
You can control multiple AWS services from the command line and automate them through scripts.

---

## 📥 Installation

### On macOS

```bash
brew install awscli
````

### On Ubuntu

```bash
sudo apt install awscli
```

---

## ⚙️ Configuration

To configure the AWS CLI, run:

```bash
aws configure
```

This command will prompt you for:

* AWS Access Key ID
* AWS Secret Access Key
* Default region name (e.g., `us-east-1`)
* Default output format (e.g., `json`)

---

## 🛠️ Basic Usage

### List all S3 buckets

```bash
aws s3 ls
```

### Start an EC2 instance

```bash
aws ec2 start-instances --instance-ids i-xxxxxxxxxxxxxxxxx
```

### Stop an EC2 instance

```bash
aws ec2 stop-instances --instance-ids i-xxxxxxxxxxxxxxxxx
```

### List Lambda functions

```bash
aws lambda list-functions
```

---

## 🧪 Notes

* Make sure your credentials are valid and have appropriate IAM permissions.
* Use `--region` flag to override default region per command.
* For advanced usage, consult the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/index.html).

```
