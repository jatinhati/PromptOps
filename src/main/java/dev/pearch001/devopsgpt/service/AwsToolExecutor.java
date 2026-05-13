package dev.pearch001.devopsgpt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AwsToolExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AwsToolExecutor.class);

    private final Ec2Client ec2Client;
    private final S3Client s3Client;
    private final CloudWatchClient cloudWatchClient;

    public AwsToolExecutor(Ec2Client ec2Client, S3Client s3Client, CloudWatchClient cloudWatchClient) {
        this.ec2Client = ec2Client;
        this.s3Client = s3Client;
        this.cloudWatchClient = cloudWatchClient;
    }

    /**
     * Starts an EC2 instance.
     * @param instanceId The ID of the instance to start.
     * @return A confirmation message.
     */
    public String startEc2Instance(String instanceId) {
        logger.info("Executing AWS action: Starting EC2 instance '{}'", instanceId);
        try {
            StartInstancesRequest request = StartInstancesRequest.builder().instanceIds(instanceId).build();
            ec2Client.startInstances(request);
            return String.format("✅ Action sent: EC2 instance %s is being started.", instanceId);
        } catch (Exception e) {
            logger.error("Failed to start EC2 instance {}: {}", instanceId, e.getMessage());
            return "❌ Error starting EC2 instance. Please check if the instance ID is correct.";
        }
    }

    /**
     * Stops an EC2 instance.
     * @param instanceId The ID of the instance to stop.
     * @return A confirmation message.
     */
    public String stopEc2Instance(String instanceId) {
        logger.info("Executing AWS action: Stopping EC2 instance '{}'", instanceId);
        try {
            StopInstancesRequest request = StopInstancesRequest.builder().instanceIds(instanceId).build();
            ec2Client.stopInstances(request);
            return String.format("✅ Action sent: EC2 instance %s is being stopped.", instanceId);
        } catch (Exception e) {
            logger.error("Failed to stop EC2 instance {}: {}", instanceId, e.getMessage());
            return "❌ Error stopping EC2 instance. Please check if the instance ID is correct.";
        }
    }

    /**
     * Lists all S3 buckets in the configured region.
     * @return A formatted string of bucket names.
     */
    public String listS3Buckets() {
        logger.info("Executing AWS action: Listing S3 buckets");
        try {
            List<String> bucketNames = s3Client.listBuckets().buckets() != null
                    ? s3Client.listBuckets().buckets().stream().map(Bucket::name).toList()
                    : List.of();

            if (bucketNames.isEmpty()) {
                return "No S3 buckets found in this AWS account.";
            }
            return "Found the following S3 buckets:\n- " + String.join("\n- ", bucketNames);
        } catch (Exception e) {
            logger.error("Failed to list S3 buckets: {}", e.getMessage());
            return "❌ Error listing S3 buckets. Please check your AWS credentials and permissions.";
        }
    }

    /**
     * Retrieves the average CPU utilization for an EC2 instance over the last hour.
     * @param instanceId The ID of the instance to monitor.
     * @return A string with the metric data or an error message.
     */
    public String getCloudWatchCpuUtilization(String instanceId) {
        logger.info("Executing AWS action: Getting CloudWatch CPU for instance '{}'", instanceId);
        try {
            GetMetricDataRequest request = GetMetricDataRequest.builder()
                    .startTime(Instant.now().minus(1, ChronoUnit.HOURS))
                    .endTime(Instant.now())
                    .metricDataQueries(
                            MetricDataQuery.builder()
                                    .id("m1")
                                    .metricStat(MetricStat.builder()
                                            .metric(Metric.builder()
                                                    .namespace("AWS/EC2")
                                                    .metricName("CPUUtilization")
                                                    .dimensions(Dimension.builder().name("InstanceId").value(instanceId).build())
                                                    .build())
                                            .period(3600)
                                            .stat("Average")
                                            .build())
                                    .returnData(true)
                                    .build())
                    .build();

            GetMetricDataResponse response = cloudWatchClient.getMetricData(request);
            if (!response.metricDataResults().isEmpty() && !response.metricDataResults().getFirst().values().isEmpty()) {
                Double avgCpu = response.metricDataResults().getFirst().values().getFirst();
                return String.format("📈 The average CPU utilization for instance %s over the last hour was %.2f%%.", instanceId, avgCpu);
            }
            return "Could not retrieve CPU data for instance " + instanceId + ". The instance may be new or metrics might be unavailable.";
        } catch (Exception e) {
            logger.error("Failed to get CloudWatch metrics for instance {}: {}", instanceId, e.getMessage());
            return "❌ Error retrieving CloudWatch metrics. Please check if the instance ID is correct and has monitoring enabled.";
        }
    }
}
