package com.mservices.catalog.repository;

import com.mservices.catalog.entity.Product;
import com.mservices.catalog.exception.ServiceException;
import com.mservices.catalog.repository.util.CriteriaSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mservices.catalog.util.ErrorConstants.CTG0100;

@Repository
public class ProductRepo extends DynamoRepo implements DynamoRecord<Product> {

    @Autowired
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;
    @Autowired
    private MessageSource messageSource;

    @Override
    public List<Product> findByCriteria(CriteriaSearch criteriaSearch) {
        List<Product> records = null;

        String expression = buildExpressionQuery(criteriaSearch);
        Map<String, AttributeValue> expValues = buildExpressionValues(criteriaSearch);

        DynamoDbTable<Product> table = getTable(dynamoDbEnhancedClient, Product.class);
        List<String> attributes = table.tableSchema().attributeNames();
        if (criteriaSearch.getExcludedFields() != null) {
            attributes = attributes.stream().filter(v -> !criteriaSearch.getExcludedFields().contains(v)).toList();
        }

        ScanEnhancedRequest req = ScanEnhancedRequest.builder()
                .consistentRead(Boolean.TRUE)
                .filterExpression(Expression.builder()
                        .expression(expression)
                        .expressionValues(expValues)
                        .build())
                .attributesToProject(attributes)
                .build();
        PageIterable<Product> pages = table.scan(req);

        if (pages.stream().count() > 0) {
            records = pages.stream().flatMap(p -> p.items().stream()).collect(Collectors.toList());
        }

        return records;
    }

    @Override
    public List<Product> findAll() {
        List<Product> records = null;

        ScanEnhancedRequest req = ScanEnhancedRequest.builder()
                .consistentRead(Boolean.TRUE)
                .build();
        PageIterable<Product> pages = getTable(dynamoDbEnhancedClient, Product.class).scan(req);

        if (pages.stream().count() > 0) {
            records = pages.stream().flatMap(p -> p.items().stream()).collect(Collectors.toList());
        }

        return records;
    }

    @Override
    public Product findRecord(String... keys) {
        Product record = null;
        if (keys != null && keys.length > 0) {
            String storeCode = keys[0];
            String productCode = keys[1];
            Key keyReq = Key.builder().partitionValue(productCode).sortValue(storeCode).build();
            record = getTable(dynamoDbEnhancedClient, Product.class).getItem(keyReq);
        }
        return record;
    }

    @Override
    public Product saveRecord(Product record) throws ServiceException {
        try {
            PutItemEnhancedRequest<Product> request = PutItemEnhancedRequest.builder(Product.class)
                    .conditionExpression(Expression.builder()
                            .expression("attribute_not_exists(storeCode)")
                            .build())
                    .item(record)
                    .build();

            getTable(dynamoDbEnhancedClient, Product.class).putItem(request);
        } catch (DynamoDbException dbException) {
            String msg = messageSource.getMessage(CTG0100, new String[] {"Product"}, LocaleContextHolder.getLocale());
            throw new ServiceException(CTG0100, msg);
        }
        return record;
    }

    @Override
    public Product updateRecord(Product record) {
        UpdateItemEnhancedRequest<Product> request = UpdateItemEnhancedRequest.builder(Product.class)
                .item(record)
                .ignoreNulls(Boolean.TRUE)
                .build();

        getTable(dynamoDbEnhancedClient, Product.class).updateItem(request);
        return record;
    }
}
