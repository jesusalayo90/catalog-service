package com.mservices.catalog.service;

import com.mservices.catalog.entity.Store;
import com.mservices.catalog.exception.ServiceException;
import com.mservices.catalog.repository.StoreRepo;
import com.mservices.catalog.repository.util.CriteriaSearch;
import com.mservices.catalog.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.mservices.catalog.repository.util.CriteriaSearch.CriteriaArg.Operator.EQUALS;
import static com.mservices.catalog.util.ErrorConstants.CTG0200;

@Service
public class StoreService {

    private final static Logger logger = LoggerFactory.getLogger(StoreService.class);

    @Autowired
    private StoreRepo storeRepo;
    @Autowired
    private ContentService contentService;
    @Autowired
    private MessageSource messageSource;

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

    public Store updateStoreImage(Store store, MultipartFile multipartFile) throws ServiceException {
        store = getStore(store.getCode());

        if (store != null && !store.getDeleted()) {
            File image = new File(multipartFile.getOriginalFilename());
            try (final FileOutputStream outputStream = new FileOutputStream(image)) {
                outputStream.write(multipartFile.getBytes());
            } catch (IOException e) {
                String msg = messageSource.getMessage(CTG0200, new String[] { ContentService.Entity.stores.toString() }, LocaleContextHolder.getLocale());
                throw new ServiceException(CTG0200, msg);
            }
            String fileName = store.getCode() + "_" + DateUtils.getCurrentTimeStamp();
            String imageUrl = contentService.sendFileToCloud(ContentService.Entity.stores, fileName, image);
            store.setImageUrl(imageUrl);
            store.setUpdatedAt(new Date());
            store = storeRepo.updateRecord(store);
        }
        return store;
    }
}
