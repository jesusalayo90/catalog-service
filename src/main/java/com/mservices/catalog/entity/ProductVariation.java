package com.mservices.catalog.entity;

import com.mservices.catalog.repository.conversion.DateConverter;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;

import java.util.Date;

import static com.mservices.catalog.util.ValidationConstants.VLD_NOT_EMPTY;
import static com.mservices.catalog.util.ValidationConstants.VLD_POSITIVE;

@Data
@DynamoDbBean
public class ProductVariation {

    @NotEmpty(message = VLD_NOT_EMPTY)
    private String name;
    @NotEmpty(message = VLD_NOT_EMPTY)
    private String imageUrl;
    @Positive(message = VLD_POSITIVE)
    private Float price;
    @PositiveOrZero(message = VLD_POSITIVE)
    private Integer stock;

    @Getter(onMethod_ = @DynamoDbConvertedBy(DateConverter.class))
    private Date createdAt;
    @Getter(onMethod_ = @DynamoDbConvertedBy(DateConverter.class))
    private Date updatedAt;
}
