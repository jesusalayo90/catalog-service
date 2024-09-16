package com.mservices.catalog.service;

import com.mservices.catalog.entity.Store;
import com.mservices.catalog.exception.ServiceException;
import com.mservices.catalog.repository.StoreRepo;
import com.mservices.catalog.repository.util.CriteriaSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

import static com.mservices.catalog.repository.util.CriteriaSearch.CriteriaArg.Operator.EQUALS;

@Service
public class StoreService {

    @Autowired
    private StoreRepo storeRepo;

    public Store getStore(String storeCode) {
        CriteriaSearch criteria = CriteriaSearch.builder()
                .addCriteria("code", storeCode, EQUALS)
                .addCriteria("deleted", Boolean.FALSE, EQUALS)
                .build();

        List<Store> result = storeRepo.findByCriteria(criteria);
        return CollectionUtils.firstElement(result);
    }

    public Store saveStore(Store store) throws ServiceException {
        if (store != null) {
            store.setDeleted(Boolean.FALSE);
            store.setCreatedAt(new Date());
        }
        return storeRepo.saveRecord(store);
    }

    public Store updateStore(Store store) {
        Store record = storeRepo.findRecord(store.getCode());
        if (record != null && !record.getDeleted()) {
            record.copy(store);
            record.setUpdatedAt(new Date());
            return storeRepo.updateRecord(record);
        }
        return null;
    }

    public Store deleteStore(Store store) {
        Store record = storeRepo.findRecord(store.getCode());
        if (record != null && !record.getDeleted()) {
            record.setDeleted(Boolean.TRUE);
            record.setUpdatedAt(new Date());
            return storeRepo.updateRecord(record);
        }
        return null;
    }
}
