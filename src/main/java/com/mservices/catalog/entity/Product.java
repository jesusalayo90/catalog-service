package com.mservices.catalog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mservices.catalog.repository.conversion.DateConverter;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.Date;
import java.util.List;

import static com.mservices.catalog.util.ValidationConstants.VLD_NOT_EMPTY;

@Data
@DynamoDbBean
public class Product {

    @NotEmpty(message = VLD_NOT_EMPTY)
    @Getter(onMethod_ = @DynamoDbPartitionKey)
    private String code;
    @NotEmpty(message = VLD_NOT_EMPTY)
    @Getter(onMethod_ = @DynamoDbSortKey)
    private String storeCode;
    @NotEmpty(message = VLD_NOT_EMPTY)
    private String name;
    @NotEmpty(message = VLD_NOT_EMPTY)
    private String imageUrl;
    private String category;
    private String detail;
    private List<ProductVariation> variations;

    @Getter(onMethod_ = @DynamoDbConvertedBy(DateConverter.class))
    private Date createdAt;
    @Getter(onMethod_ = @DynamoDbConvertedBy(DateConverter.class))
    private Date updatedAt;
    @JsonIgnore
    private Boolean deleted;

}
