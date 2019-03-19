package com.easy.jobservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "authority")
public class Authority implements Serializable, GrantedAuthority {

    private static final long serialVersionUID = 1L;

    @Id
    private String name;

    @Override
    public String getAuthority() {
        return this.name;
    }
}
