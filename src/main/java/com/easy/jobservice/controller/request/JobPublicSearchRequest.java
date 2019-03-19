package com.easy.jobservice.controller.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPublicSearchRequest extends BaseSearchRequest{

    private String point;

}
