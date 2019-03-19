package com.easy.jobservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobUploadPhotoResponse {

    private String url;
    private String thumbnail;

}
