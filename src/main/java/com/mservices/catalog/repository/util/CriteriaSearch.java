package com.mservices.catalog.repository.util;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CriteriaSearch {

    private final List<CriteriaArg> criteriaArgs;
    private final List<String> excludedFields;

    private CriteriaSearch(Builder builder) {
        this.criteriaArgs = builder.criteriaArgs;

        if (StringUtils.hasText(builder.excludeFields)) {
            this.excludedFields = new ArrayList<>(Arrays.asList(builder.excludeFields.split(",")));
        } else {
            this.excludedFields = null;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<CriteriaArg> getCriteriaArgs() {
        return criteriaArgs;
    }

    public List<String> getExcludedFields() {
        return excludedFields;
    }

    @Data
    public static class CriteriaArg {

        public enum Operator {
            EQUALS("=");

            String value;

            Operator(String value) { this.value = value; }

            public String getValue() { return value; }
        }

        private String field;
        private Object value;
        private Operator operator;
    }

    public static class Builder {

        private List<CriteriaArg> criteriaArgs;
        private String excludeFields;

        public Builder addCriteria(String field, Object value, CriteriaArg.Operator operator) {
            if (criteriaArgs == null) {
                criteriaArgs = new ArrayList<>();
            }
            CriteriaArg criteria = new CriteriaArg();
            criteria.setField(field);
            criteria.setValue(value);
            criteria.setOperator(operator);
            criteriaArgs.add(criteria);
            return this;
        }

        public Builder excludeFields(String fields) {
            excludeFields = fields;
            return this;
        }

        public CriteriaSearch build() {
            return new CriteriaSearch(this);
        }

    }
}
