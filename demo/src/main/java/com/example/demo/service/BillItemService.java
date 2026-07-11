package com.example.demo.service;

import com.example.demo.entity.BillItem;
import com.example.demo.repository.BillItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillItemService {

    private final BillItemRepository billItemRepository;

    public BillItemService(BillItemRepository billItemRepository) {
        this.billItemRepository = billItemRepository;
    }

    public BillItem saveItem(BillItem item) {
        return billItemRepository.save(item);
    }

    public List<BillItem> getAllItems() {
        return billItemRepository.findAll();
    }

    public Optional<BillItem> getItemById(Long id) {
        return billItemRepository.findById(id);
    }

    public void deleteItem(Long id) {
        billItemRepository.deleteById(id);
    }
}