package com.example.demo.service;

import com.example.demo.dto.OCRBillItem;
import com.example.demo.dto.OCRBillResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class OCRService {

    private final RestClient restClient;

    @Value("${veryfi.client-id}")
    private String clientId;

    @Value("${veryfi.username}")
    private String username;

    @Value("${veryfi.api-key}")
    private String apiKey;

    public OCRService() {

        this.restClient = RestClient.builder()
                .baseUrl("https://api.veryfi.com/api/v8/partner")
                .build();
    }

    public OCRBillResponse process(MultipartFile file)
            throws IOException {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {

            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        body.add("file", resource);

        Map response = restClient.post()
                .uri("/documents")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("CLIENT-ID", clientId)
                .header(
                        "AUTHORIZATION",
                        "apikey " + username + ":" + apiKey)
                .body(body)
                .retrieve()
                .body(Map.class);

        System.out.println("VERYFI RESPONSE:");
        System.out.println(response);

        return convertResponse(response, file);
    }

    private OCRBillResponse convertResponse(
            Map response,
            MultipartFile file) {

        OCRBillResponse result = new OCRBillResponse();

        result.setUploadedFilePath(
                file.getOriginalFilename());

        // -----------------------------
        // Bill number
        // -----------------------------

        result.setBillNumber(
                getString(response, "invoice_number"));

        // -----------------------------
        // Date
        // -----------------------------

        String date = getString(response, "date");

        if (date != null && !date.isBlank()) {

            try {

                result.setBillDate(
                        LocalDate.parse(
                                date.substring(0, 10)));

            } catch (Exception ignored) {
            }
        }

        // -----------------------------
        // Amounts
        // -----------------------------

        result.setSubtotal(
                getBigDecimal(response, "subtotal"));

        result.setTotal(
                getBigDecimal(response, "total"));

        // -----------------------------
        // Vendor / Hospital
        // -----------------------------

        Object vendorObject = response.get("vendor");

        if (vendorObject instanceof Map vendor) {

            String vendorName = getString(vendor, "name");

            if (vendorName == null) {

                vendorName = getString(vendor, "raw_name");
            }

            result.setHospitalName(vendorName);

        } else {

            result.setHospitalName(
                    getString(response, "vending_person"));
        }

        // -----------------------------
        // Line Items
        // -----------------------------

        Object lineItemsObject = response.get("line_items");

        if (lineItemsObject instanceof List<?> lineItems) {

            for (Object object : lineItems) {

                if (!(object instanceof Map itemMap)) {
                    continue;
                }

                OCRBillItem item = new OCRBillItem();

                item.setDescription(
                        getString(
                                itemMap,
                                "description"));

                item.setPrice(
                        getBigDecimal(
                                itemMap,
                                "price"));

                Number quantity = getNumber(
                        itemMap,
                        "quantity");

                if (quantity != null) {

                    item.setQuantity(
                            quantity.intValue());

                } else {

                    item.setQuantity(1);
                }

                result.getItems().add(item);
            }
        }

        return result;
    }

    private String getString(
            Map map,
            String key) {

        Object value = map.get(key);

        return value == null
                ? null
                : value.toString();
    }

    private BigDecimal getBigDecimal(
            Map map,
            String key) {

        Object value = map.get(key);

        if (value == null) {
            return null;
        }

        try {

            return new BigDecimal(
                    value.toString());

        } catch (Exception e) {

            return null;
        }
    }

    private Number getNumber(
            Map map,
            String key) {

        Object value = map.get(key);

        if (value instanceof Number number) {
            return number;
        }

        if (value != null) {

            try {

                return Double.parseDouble(
                        value.toString());

            } catch (Exception ignored) {
            }
        }

        return null;
    }
}