package com.mservices.catalog.repository.extension;

import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTag;

public final class UuidGeneratorTag {

    private UuidGeneratorTag() {}

    public static StaticAttributeTag attributeTagFor(UuidGenerator annotation) {
        return UuidGeneratorExtension.AttributeTags.uuidGeneratorAttribute(annotation.behavior());
    }
}
