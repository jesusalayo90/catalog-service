package com.mservices.catalog.repository.util;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class CriteriaSearch {

    private final List<CriteriaArg> criteriaArgs;

    private CriteriaSearch(List<CriteriaArg> criteriaArgs) {
        this.criteriaArgs = criteriaArgs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<CriteriaArg> getCriteriaArgs() {
        return criteriaArgs;
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

        public CriteriaSearch build() {
            return new CriteriaSearch(this.criteriaArgs);
        }

    }
}
