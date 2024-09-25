package com.mservices.catalog.repository.extension;

import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClientExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbExtensionContext;
import software.amazon.awssdk.enhanced.dynamodb.extensions.WriteModification;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTag;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableMetadata;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class UuidGeneratorExtension implements DynamoDbEnhancedClientExtension {

    private static final String CUSTOM_METADATA_KEY =
            "UuidGeneratorExtension.UuidGeneratorAttribute:";

    @Override
    public WriteModification beforeWrite(DynamoDbExtensionContext.BeforeWrite context) {
        Map<String, Object> customMetadataObject = context.tableMetadata()
                .customMetadata().entrySet()
                .stream().filter(e -> e.getKey().startsWith(CUSTOM_METADATA_KEY))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (customMetadataObject == null) {
            return WriteModification.builder().build();
        }
        Map<String, AttributeValue> itemToTransform = new HashMap<>(context.items());
        for (Map.Entry<String, Object> entry : customMetadataObject.entrySet()) {
            String field = entry.getKey().split(":")[1];
            GeneratorBehavior behavior = (GeneratorBehavior) entry.getValue();

            if (GeneratorBehavior.ONLY_IF_EMPTY.equals(behavior)) {
                if (itemToTransform.get(field).s() == null) {
                    itemToTransform.put(field, AttributeValue.builder().s(UUID.randomUUID().toString()).build());
                }
            } else if (GeneratorBehavior.ALWAYS.equals(behavior)) {
                itemToTransform.put(field, AttributeValue.builder().s(UUID.randomUUID().toString()).build());
            }
        }

        return WriteModification.builder()
                .transformedItem(Collections.unmodifiableMap(itemToTransform))
                .build();
    }

    public static final class AttributeTags {

        private AttributeTags() {
        }

        public static StaticAttributeTag uuidGeneratorAttribute(GeneratorBehavior behavior) {
            return UuidGeneratorAttribute.fromBehavior(behavior);
        }
    }

    private static class UuidGeneratorAttribute implements StaticAttributeTag {

        private static final UuidGeneratorAttribute WRITE_ALWAYS_TAG = new UuidGeneratorAttribute(GeneratorBehavior.ALWAYS);
        private static final UuidGeneratorAttribute WRITE_ONLY_IF_EMPTY_TAG =
                new UuidGeneratorAttribute(GeneratorBehavior.ONLY_IF_EMPTY);
        private final GeneratorBehavior generatorBehavior;

        private UuidGeneratorAttribute(GeneratorBehavior generatorBehavior) {
            this.generatorBehavior = generatorBehavior;
        }

        public static UuidGeneratorAttribute fromBehavior(GeneratorBehavior generatorBehavior) {
            switch (generatorBehavior) {
                case ALWAYS -> {
                    return WRITE_ALWAYS_TAG;
                }
                case ONLY_IF_EMPTY -> {
                    return WRITE_ONLY_IF_EMPTY_TAG;
                }
                default -> throw new IllegalArgumentException("Generator behavior '" + generatorBehavior + "' not supported");
            }
        }

        @Override
        public Consumer<StaticTableMetadata.Builder> modifyMetadata(String attributeName, AttributeValueType attributeValueType) {
            return metadata -> metadata
                    .addCustomMetadataObject(CUSTOM_METADATA_KEY + attributeName, this.generatorBehavior)
                    .markAttributeAsKey(attributeName, attributeValueType);
        }
    }
}
