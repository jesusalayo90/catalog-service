package com.mservices.catalog.service;

import com.mservices.catalog.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;

import static com.mservices.catalog.util.ErrorConstants.CTG0201;

@Service
public class ContentService {

    public enum Entity { stores, products }

    private final static Logger logger = LoggerFactory.getLogger(ContentService.class);

    @Autowired
    private S3Client s3Client;
    @Autowired
    private MessageSource messageSource;
    @Value("${amazon.aws.s3.bucket.catalog}")
    private String BUCKET_CATALOG;
    @Value("${amazon.aws.cloudfront.cdn.catalog}")
    private String CONT_DELV_NETW_CATALOG_URL;

    /**
     * @return Cloud url for the file
     */
    public String sendFileToCloud(Entity entity, String fileName, File file) throws ServiceException {
        fileName = entity + "/" + fileName;
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(BUCKET_CATALOG)
                .key(fileName)
                .build();

        try {
            PutObjectResponse resp = s3Client.putObject(req, RequestBody.fromFile(file));
        } catch (AwsServiceException awsServiceException) {
            String msg = messageSource.getMessage(CTG0201, new String[] { entity.toString() }, LocaleContextHolder.getLocale());
            logger.error(msg + ". Server failed");
            throw new ServiceException(CTG0201, msg);
        } catch (SdkClientException sdkClientException) {
            String msg = messageSource.getMessage(CTG0201, new String[] { entity.toString() }, LocaleContextHolder.getLocale());
            logger.error(msg);
            throw new ServiceException(CTG0201, msg);
        }

        return String.format("%s/%s", CONT_DELV_NETW_CATALOG_URL, fileName);
    }
}
