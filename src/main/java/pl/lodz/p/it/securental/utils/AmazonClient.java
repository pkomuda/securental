package pl.lodz.p.it.securental.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.mor.FileUploadException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

@Component
public class AmazonClient {

    private AmazonS3 s3Client;

    @Value("${s3.bucket.region}")
    private String bucketRegion;

    @Value("${s3.bucket.name}")
    private String bucketName;

    @Value("${s3.access.key}")
    private String accessKey;

    @Value("${s3.secret.key}")
    private String secretKey;

    @PostConstruct
    private void init() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3Client = new AmazonS3Client(credentials);
    }

    public String uploadFile(String name, MultipartFile multipartFile) throws ApplicationBaseException {
        File file = multipartToFile(multipartFile);
        String fileName = Instant.now().toEpochMilli() + "-" + name + "-" + StringUtils.randomIdentifier();
        String fileUrl = "https://s3." + bucketRegion + ".amazonaws.com/" + bucketName + "/" + fileName;
        uploadFileToS3(fileName, file);
        if (!file.delete()) {
            file.deleteOnExit();
        }
        return fileUrl;
    }

    private File multipartToFile(MultipartFile multipartFile) throws ApplicationBaseException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            throw new FileUploadException(e);
        }
        return file;
    }

    private void uploadFileToS3(String fileName, File file) {
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }
}
