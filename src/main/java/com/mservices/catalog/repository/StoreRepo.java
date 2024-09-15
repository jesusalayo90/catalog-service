package com.mservices.catalog.repository;

import com.mservices.catalog.entity.Store;
import com.mservices.catalog.exception.ServiceException;
import com.mservices.catalog.repository.util.CriteriaSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mservices.catalog.util.ErrorConstants.CTG0100;

@Repository
public class StoreRepo extends DynamoRepo implements DynamoRecord<Store> {

    @Autowired
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;
    @Autowired
    private MessageSource messageSource;

    @Override
    public List<Store> findByCriteria(CriteriaSearch criteriaSearch) {
        List<Store> records = null;

        String expression = buildExpressionQuery(criteriaSearch);
        Map<String, AttributeValue> expValues = buildExpressionValues(criteriaSearch);

        ScanEnhancedRequest req = ScanEnhancedRequest.builder()
                .consistentRead(Boolean.TRUE)
                .filterExpression(Expression.builder()
                        .expression(expression)
                        .expressionValues(expValues)
                        .build())
                .build();
        PageIterable<Store> pages = getTable(dynamoDbEnhancedClient, Store.class).scan(req);

        if (pages.stream().count() > 0) {
            records = pages.stream().flatMap(p -> p.items().stream()).collect(Collectors.toList());
        }

        return records;
    }

    @Override
    public List<Store> findAll() {
        List<Store> records = null;

        ScanEnhancedRequest req = ScanEnhancedRequest.builder()
                .consistentRead(Boolean.TRUE)
                .build();
        PageIterable<Store> pages = getTable(dynamoDbEnhancedClient, Store.class).scan(req);

        if (pages.stream().count() > 0) {
            records = pages.stream().flatMap(p -> p.items().stream()).collect(Collectors.toList());
        }

        return records;
    }

    @Override
    public Store findRecord(String... keys) {
        Store record = null;
        if (keys != null && keys.length > 0) {
            String key = keys[0];
            record = getTable(dynamoDbEnhancedClient, Store.class).getItem(Key.builder().partitionValue(key).build());
        }
        return record;
    }

    @Override
    public Store saveRecord(Store record) throws ServiceException {
        try {
            PutItemEnhancedRequest<Store> request = PutItemEnhancedRequest.builder(Store.class)
                    .conditionExpression(Expression.builder()
                            .expression("attribute_not_exists(storeCode)")
                            .build())
                    .item(record)
                    .build();

            getTable(dynamoDbEnhancedClient, Store.class).putItem(request);
        } catch (DynamoDbException dbException) {
            String msg = messageSource.getMessage(CTG0100, new String[] {"StoreReview"}, LocaleContextHolder.getLocale());
            throw new ServiceException(CTG0100, msg);
        }
        return record;
    }

    @Override
    public Store updateRecord(Store record) {
        getTable(dynamoDbEnhancedClient, Store.class).updateItem(record);
        return record;
    }
}
