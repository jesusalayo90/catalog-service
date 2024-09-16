package com.mservices.catalog.controller;

import com.mservices.catalog.entity.Product;
import com.mservices.catalog.exception.ServiceException;
import com.mservices.catalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.mservices.catalog.controller.util.BindingResultUtil.formatErrors;

@Controller
@RequestMapping(value = "/products")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> listProducts(@RequestParam(name = "storeCode", required = false) String storeCode,
                                                      @RequestParam(name = "category", required = false) String category) {
        List<Product> list = productService.listProductCatalog(storeCode, category);
        return list != null ? ResponseEntity.ok(list) : ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable(name = "productId") String productId,
                                              @RequestParam(name = "storeCode") String storeCode,
                                              @RequestParam(name = "detail", required = false, defaultValue = "false") Boolean detail) {
        Product product = productService.getProduct(storeCode, productId, detail);
        return product != null? ResponseEntity.ok(product) : ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product, BindingResult validation) throws ServiceException {
        if (validation.hasErrors()) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, formatErrors(validation));
        }
        product = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping(value = "/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable(name = "productId") String productId,
                                                 @Valid @RequestBody Product product, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, formatErrors(validation));
        }
        product.setCode(productId);
        product = productService.updateProduct(product);
        return product != null? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/{productId}")
    public ResponseEntity<Product> deleteProduct(@PathVariable(name = "productId") String productId, @RequestParam(name = "storeCode") String storeCode) {
        Product product = new Product();
        product.setCode(productId);
        product.setStoreCode(storeCode);
        product = productService.deleteProduct(product);
        return product != null? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }
}
