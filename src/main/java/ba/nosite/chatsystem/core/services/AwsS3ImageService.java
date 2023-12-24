package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.services.redisServices.RedisHashService;
import ba.nosite.chatsystem.customTypes.Tuple2;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static ba.nosite.chatsystem.utils.TimeConversion.convertHourToMs;

@Service
public class AwsS3ImageService {
    private final AmazonS3 s3Client;
    private final RedisHashService redisHashService;
    private final Logger logger;
    @Value("${aws.s3.bucket}")
    private String bucketName;
    @Value("${aws.s3.expirationHours}")
    private Long s3ImageExpirationHours;
    @Value("${aws.s3.expirationThresholdInMs}")
    private Long s3ImageExpirationThresholdInMs;

    public AwsS3ImageService(AmazonS3 s3Client, RedisHashService redisHashService) {
        this.s3Client = s3Client;
        this.redisHashService = redisHashService;
        logger = LoggerFactory.getLogger(AwsS3ImageService.class);
    }

    /**
     * Uploads a file image to an S3 Bucket.
     *
     * @param file The MultipartFile representing the image file.
     * @return A Tuple2 containing the file URL and expiration time.
     * @throws RuntimeException If an error occurs during the upload.
     */
    public Tuple2<String, Date> uploadImage(MultipartFile file) {
        String key = "server_images/"
                .concat(UUID.randomUUID().toString())
                .concat("-")
                .concat(Objects.requireNonNull(file.getOriginalFilename()));

        long contentLength = file.getSize();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);

        try {
            s3Client.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Date expirationTime = generateExpirationTime();

        return new Tuple2<>(generatePreSignedUrl(expirationTime, key), expirationTime);
    }

    /**
     * Refreshes the avatar icon URL for a given URL if it has expired.
     *
     * @param avatarIconUrl Avatar Icon URL to refresh.
     * @return A Tuple2 containing the refreshed avatar icon URL and its new expiration time, or null if not refreshed.
     */
    public Tuple2<String, Date> refreshAvatarIconUrl(String avatarIconUrl) {
        if (isUrlExpired(avatarIconUrl)) {
            Tuple2<String, Date> result = doRefresh(avatarIconUrl);
            String refreshedAvatarIconUrl = result.getFirst();
            Date expirationTime = result.getSecond();

            return new Tuple2<>(refreshedAvatarIconUrl, expirationTime);
        }
        return null;
    }

    /**
     * Deletes an image from the S3 bucket based on its URL.
     *
     * @param imageUrl The URL of the image to be deleted.
     */
    public void deleteImage(String imageUrl) {
        if (StringUtils.isNotBlank(imageUrl)) {
            String key = extractKeyFromImageUrl(imageUrl);
            deleteImageFromS3(key);
        }
    }

    private void deleteImageFromS3(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
            logger.info("Image deleted from S3 successfully.");
        } catch (Exception e) {
            logger.error("Error deleting image from S3: {}", e.getMessage());
        }
    }

    private Tuple2<String, Date> doRefresh(String avatarIconUrl) {
        Date newExpirationTime = generateExpirationTime();

        logger.info("Refreshing avatar icon URL for - {}", avatarIconUrl);
        String fileName = "server_images/" + Objects.requireNonNull(extractFileNameFromUrl(avatarIconUrl));
        String refreshedUrl = generatePreSignedUrl(newExpirationTime, fileName);

        // Use consistent keys for expiration times in Redis
        String redisKey = "avatarIconUrlExpirationTimes";

        redisHashService.delete(avatarIconUrl, redisKey);
        redisHashService.putWithoutExpire(redisKey, refreshedUrl, newExpirationTime.getTime());

        return new Tuple2<>(refreshedUrl, newExpirationTime);
    }

    private String generatePreSignedUrl(Date exp, String fileName) {
        GeneratePresignedUrlRequest urlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(exp);

        return s3Client.generatePresignedUrl(urlRequest).toString();
    }

    private Date generateExpirationTime() {
        Date exp = new Date();
        long exTimeMs = exp.getTime();

        exTimeMs += convertHourToMs(s3ImageExpirationHours);
        exp.setTime(exTimeMs);
        return exp;
    }

    private String extractKeyFromImageUrl(String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            String path = uri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path;
        } catch (URISyntaxException e) {
            logger.error("Error parsing URL: ".concat(e.getMessage()));
            return null;
        }
    }

    private String extractFileNameFromUrl(String url) {
        int lastSlashIndex = url.lastIndexOf("/");
        int lastQuestionMarkIndex = url.lastIndexOf("?");
        if (lastSlashIndex != -1 && lastQuestionMarkIndex != -1 && lastSlashIndex < lastQuestionMarkIndex) {
            return url.substring(lastSlashIndex + 1, lastQuestionMarkIndex);
        }
        return null;
    }

    private boolean isUrlExpired(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        String redisKey = "avatarIconUrlExpirationTimes";
        Long expirationTime = redisHashService.get(redisKey, url, Long.class);

        // System.out.println("is expired " + (System.currentTimeMillis() <= expirationTime));

        if (expirationTime != null) {
            return System.currentTimeMillis() <= expirationTime;
        } else {
            logger.info("URL not found in the map: {}", url);

            long newExpirationTime = System.currentTimeMillis() + s3ImageExpirationThresholdInMs;
            redisHashService.putWithoutExpire(redisKey, url, newExpirationTime);
            return false;
        }
    }
}