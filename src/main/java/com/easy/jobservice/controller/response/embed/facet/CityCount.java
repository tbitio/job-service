package com.easy.jobservice.controller.response.embed.facet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CityCount {

    public String city;
    public long n = 0;

}
