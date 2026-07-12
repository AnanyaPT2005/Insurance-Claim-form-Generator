package com.example.demo.controller.api;

import com.example.demo.entity.BillItem;
import com.example.demo.service.BillItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/bill-items")
public class BillItemController {

    private final BillItemService billItemService;

    public BillItemController(BillItemService billItemService) {
        this.billItemService = billItemService;
    }

    @GetMapping
    public List<BillItem> getAllItems() {
        return billItemService.getAllItems();
    }

    @GetMapping("/{id}")
    public Optional<BillItem> getItem(@PathVariable Long id) {
        return billItemService.getItemById(id);
    }

    @PostMapping
    public BillItem saveItem(@RequestBody BillItem item) {
        return billItemService.saveItem(item);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        billItemService.deleteItem(id);
    }
}