package com.mservices.catalog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mservices.catalog.repository.conversion.DateConverter;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Date;

import static com.mservices.catalog.util.ValidationConstants.VLD_NOT_EMPTY;

@Data
@DynamoDbBean
public class Store {

    @NotEmpty(message = VLD_NOT_EMPTY)
    @Getter(onMethod_ = @DynamoDbPartitionKey)
    private String code;
    @NotEmpty(message = VLD_NOT_EMPTY)
    private String name;
    private String imageUrl;
    private String country;
    private Integer followers;

    @Getter(onMethod_ = @DynamoDbConvertedBy(DateConverter.class))
    private Date createdAt;
    @Getter(onMethod_ = @DynamoDbConvertedBy(DateConverter.class))
    private Date updatedAt;
    @JsonIgnore
    private Boolean deleted;

    public void copy(Store s, boolean clone) {
        copy(s);
        if (clone) {
            this.setCode(s.getCode());
            this.setDeleted(s.getDeleted());
            this.setCreatedAt(s.getCreatedAt());
        }
    }

    public void copy(Store s) {
        this.setName(s.getName());
        this.setImageUrl(s.getImageUrl());
        this.setCountry(s.getCountry());
        this.setFollowers(s.getFollowers());
        this.setUpdatedAt(s.getUpdatedAt());
    }
}
