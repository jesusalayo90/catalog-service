package com.mservices.catalog.repository.conversion;

import com.mservices.catalog.util.DateUtils;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Date;

public class DateConverter implements AttributeConverter<Date> {

    @Override
    public AttributeValue transformFrom(Date date) {
        String value = DateUtils.fromDate(date, DateUtils.DATE_TIME_ZONE);
        return AttributeValue.fromS(value);
    }

    @Override
    public Date transformTo(AttributeValue attributeValue) {
        String value = attributeValue.s();
        return DateUtils.parseDate(value, DateUtils.DATE_TIME_ZONE);
    }

    @Override
    public EnhancedType<Date> type() {
        return EnhancedType.of(Date.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
