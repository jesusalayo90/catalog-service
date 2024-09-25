package com.mservices.catalog.config;

import com.mservices.catalog.repository.extension.UuidGeneratorExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.internal.client.ExtensionResolver;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class AwsServiceConfig {
    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey;
    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey;
    @Value("${amazon.aws.region}")
    private String amazonAWSRegion;

    @Bean
    public S3Client s3Client(AwsCredentialsProvider awsCredentialsProvider) {
        Region region = Region.of(amazonAWSRegion);
        return S3Client.builder()
                .region(region)
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean
    public DynamoDbClient dynamoDbClient(AwsCredentialsProvider awsCredentialsProvider) {
        Region region = Region.of(amazonAWSRegion);
        return DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .extensions(Stream.concat(ExtensionResolver.defaultExtensions().stream(),
                        Stream.of(new UuidGeneratorExtension())).collect(Collectors.toList()))
                .build();
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        AwsCredentials awsCredentials = AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey);
        return StaticCredentialsProvider.create(awsCredentials);
    }
}
