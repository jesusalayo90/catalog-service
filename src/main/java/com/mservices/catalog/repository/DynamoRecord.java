package com.mservices.catalog.repository;

import com.mservices.catalog.exception.ServiceException;
import com.mservices.catalog.repository.util.CriteriaSearch;

import java.util.List;

public interface DynamoRecord<T> {

    List<T> findByCriteria(CriteriaSearch criteriaSearch);
    List<T> findAll();
    T findRecord(String... keys);
    T saveRecord(T record) throws ServiceException;
    T updateRecord(T record);
}
