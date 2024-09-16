package com.mservices.catalog.service;

import com.mservices.catalog.entity.Product;
import com.mservices.catalog.entity.ProductVariation;
import com.mservices.catalog.exception.ServiceException;
import com.mservices.catalog.repository.ProductRepo;
import com.mservices.catalog.repository.util.CriteriaSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.mservices.catalog.repository.util.CriteriaSearch.CriteriaArg.Operator.EQUALS;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    public List<Product> listProductCatalog(String storeCode, String category) {
        CriteriaSearch.Builder builder = CriteriaSearch.builder()
                .addCriteria("deleted", Boolean.FALSE, EQUALS)
                .excludeFields("detail,variations");

        if (StringUtils.hasText(storeCode)) builder.addCriteria("storeCode", storeCode, EQUALS);
        if (StringUtils.hasText(category)) builder.addCriteria("category", storeCode, EQUALS);

        List<Product> reviews = productRepo.findByCriteria(builder.build());

        return Optional.ofNullable(reviews).orElse(new ArrayList<>());
    }

    public Product getProduct(String storeCode, String productCode, boolean detail) {
        CriteriaSearch.Builder builder = CriteriaSearch.builder()
                .addCriteria("code", productCode, EQUALS)
                .addCriteria("storeCode", storeCode, EQUALS)
                .addCriteria("deleted", Boolean.FALSE, EQUALS);

        if (!detail) {
            builder.excludeFields("detail,variations");
        }
        List<Product> result = productRepo.findByCriteria(builder.build());
        return CollectionUtils.firstElement(result);
    }

    public Product saveProduct(Product product) throws ServiceException {
        if (product != null) {
            product.setCreatedAt(new Date());
            product.setDeleted(Boolean.FALSE);

            if (product.getVariations() != null) {
                for (ProductVariation var : product.getVariations()) {
                    var.setCreatedAt(new Date());
                }
            }

        }
        return productRepo.saveRecord(product);
    }

    public Product updateProduct(Product product) {
        Product record = productRepo.findRecord(product.getStoreCode(), product.getCode());
        if (record != null && !record.getDeleted()) {
            record.copy(product);
            record.setUpdatedAt(new Date());

            if (product.getVariations() != null) {
                for (ProductVariation var : record.getVariations()) {
                    var.setUpdatedAt(new Date());
                }
            }

            return productRepo.updateRecord(record);
        }
        return null;
    }

    public Product deleteProduct(Product product) {
        Product record = productRepo.findRecord(product.getStoreCode(), product.getCode());
        if (record != null && !record.getDeleted()) {
            record.setUpdatedAt(new Date());
            record.setDeleted(Boolean.TRUE);
            return productRepo.updateRecord(record);
        }
        return null;
    }
}
