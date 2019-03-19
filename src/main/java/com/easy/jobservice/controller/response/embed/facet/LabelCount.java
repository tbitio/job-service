package com.easy.jobservice.controller.response.embed.facet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabelCount {


    public String label;
    public long n = 0;

}
