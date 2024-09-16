package com.mservices.catalog.controller;

import com.mservices.catalog.entity.Store;
import com.mservices.catalog.exception.ServiceException;
import com.mservices.catalog.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static com.mservices.catalog.controller.util.BindingResultUtil.formatErrors;

@Controller
@RequestMapping(value = "/stores")
public class StoreController extends BaseController {

    @Autowired
    private StoreService storeService;

    @GetMapping(value = "/{storeCode}")
    public ResponseEntity<Store> getStore(@PathVariable(name = "storeCode") String storeCode) {
        Store store = storeService.getStore(storeCode);
        return store != null? ResponseEntity.ok(store) : ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Store> createStore(@Valid @RequestBody Store store, BindingResult validation) throws ServiceException {
        if (validation.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, formatErrors(validation));
        }
        store = storeService.saveStore(store);
        return ResponseEntity.status(HttpStatus.CREATED).body(store);
    }

    @PutMapping(value = "/{storeCode}")
    public ResponseEntity<Store> updateStore(@PathVariable(name = "storeCode") String storeCode, @Valid @RequestBody Store store, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, formatErrors(validation));
        }
        store.setCode(storeCode);
        store = storeService.updateStore(store);
        return store != null? ResponseEntity.ok(store) : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/{storeCode}")
    public ResponseEntity<Store> deleteStore(@PathVariable(name = "storeCode") String storeCode) {
        Store store = new Store();
        store.setCode(storeCode);
        store = storeService.deleteStore(store);
        return store != null? ResponseEntity.ok(store) : ResponseEntity.notFound().build();
    }
}
