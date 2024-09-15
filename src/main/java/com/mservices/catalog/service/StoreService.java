package com.mservices.catalog.service;

import com.mservices.catalog.entity.Store;
import com.mservices.catalog.exception.ServiceException;
import com.mservices.catalog.repository.StoreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StoreService {

    @Autowired
    private StoreRepo storeRepo;

    public Store getStore(String code, Boolean includeDetail) {
        Store store = null;
        if (includeDetail) {
            store = storeRepo.findRecord(code);
        } else {
            //TODO exclude detail field in db query
        }
        return store;
    }

    public Store saveStore(Store store) throws ServiceException {
        if (store != null) {
            store.setDeleted(Boolean.FALSE);
            store.setCreatedAt(new Date());
        }
        return storeRepo.saveRecord(store);
    }

    public Store updateStore(Store store) {
        Store record = getStore(store.getCode(), Boolean.TRUE);
        if (record != null && !record.getDeleted()) {
            record.copy(store);
            return storeRepo.updateRecord(record);
        }
        return null;
    }

    public Store deleteStore(Store store) {
        Store record = getStore(store.getCode(), Boolean.TRUE);
        if (record != null && !record.getDeleted()) {
            record.setDeleted(Boolean.TRUE);
            return storeRepo.updateRecord(record);
        }
        return null;
    }
}
