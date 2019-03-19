package com.easy.jobservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.easy.jobservice.service.UploadService;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {

    @Value("${aws.bucket}")
    private String s3Bucket;

    @Value("${aws.endpoint}")
    private String s3endpoint;

    private final AmazonS3 s3client;

    @Autowired
    public UploadServiceImpl(AmazonS3 s3client) {
        this.s3client = s3client;
    }

    @Override
    public String uploadPhoto(File photo) {
        String fileExtension = "PNG";
        String filename = String.format("%s.%s", UUID.randomUUID(), fileExtension);

        s3client.putObject(
                new PutObjectRequest(s3Bucket, filename, photo)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        return String.format("%s/%s/%s", s3endpoint, s3Bucket, filename);
    }

    @Override
    public File getThumbnail(File photo) {
        try {
            BufferedImage image = ImageIO.read(photo);

            int width = 300;
            int height = 200;

            BufferedImage thumbImg = Scalr.resize(
                    image,
                    Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC,
                    width,
                    height,
                    Scalr.OP_ANTIALIAS
            );

            File result = File.createTempFile(
                    String.format("%s_", FilenameUtils.getBaseName(photo.getName())),
                    String.format(".%s", FilenameUtils.getExtension(photo.getName()))
            );

            ImageIO.write(thumbImg, FilenameUtils.getExtension(photo.getName()), result);

            return result;

        } catch (IOException ex) {
            return null;
        }
    }
}
