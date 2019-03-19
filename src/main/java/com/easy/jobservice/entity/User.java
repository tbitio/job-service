package com.easy.jobservice.entity;

import com.easy.jobservice.common.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@Document(collection = "user")
public class User extends BaseEntity<String> implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("username")
    private String username;

    @Field("password")
    private String password;

    @Field("last_name")
    private String name;

    @Indexed
    private String email;

    @Field("activated")
    private boolean activated;

    @Field("lang_key")
    private String langKey;

    @Field("image_url")
    private String imageUrl;

    @Field("activation_key")
    private String activationKey;

    @Field("reset_key")
    private String resetKey;

    @Field("reset_date")
    private Instant resetDate;

    @Field("wallet_id")
    private String walletID;

    private boolean telegram = false;

    private Integer telegramId;

    private String telegramUsername;

    private Set<Authority> authorities;

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.activated;
    }

    @Override
    public boolean isEnabled() {
        return this.activated;
    }
}
