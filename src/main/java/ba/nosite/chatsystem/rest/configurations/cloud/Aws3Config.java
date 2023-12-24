package ba.nosite.chatsystem.rest.configurations.cloud;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Aws3Config {
    Logger logger = LoggerFactory.getLogger(Aws3Config.class);

    @Value("${aws.access-key-id}")
    private String awsAccessKeyId;

    @Value("${aws.secret-access-key}")
    private String awsSecretAccessKey;

    @Value("${aws.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3Client() {
        logger.info("Creating Amazon S3 client. Access Key ID: "
                .concat(awsAccessKeyId)
                .concat("Region: ")
                .concat(region));


        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);

        AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region)
                .build();

        logger.info("Amazon S3 client created successfully.");

        return amazonS3Client;
    }
}
