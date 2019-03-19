package com.easy.jobservice.service;

import java.io.File;

/**
 * The {@code UploadService} interface provides methods in order to upload images to Amazon S3 bucket.
 */
public interface UploadService {

    /**
     * Uploads the specified photo to Amazon S3 bucket.
     *
     * @param photo a {@link File} object.
     * @return a public URL for a new photo.
     */
    String uploadPhoto(File photo);

    /**
     *
     * @param photo a {@link File} object.
     * @return a thumbnail of photo.
     */
    File getThumbnail(File photo);

}
