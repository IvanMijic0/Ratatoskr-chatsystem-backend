package ba.nosite.chatsystem.core.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Service
public class AwsS3ImageService {
    private final AmazonS3 s3Client;
    private final Logger logger = LoggerFactory.getLogger(AwsS3ImageService.class);

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public AwsS3ImageService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadImage(MultipartFile file, String fileName) throws IOException {
        String key = "server_images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), new ObjectMetadata()));
        return key;
    }

    public void deleteImage(String imageUrl) {
        String key = extractKeyFromImageUrl(imageUrl);
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
            logger.info("Image deleted from S3 successfully.");
        } catch (Exception e) {
            logger.error("Error deleting image from S3: " + e.getMessage());
        }
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
            logger.error("Error parsing URL: " + e.getMessage());
            return null;
        }
    }
}
