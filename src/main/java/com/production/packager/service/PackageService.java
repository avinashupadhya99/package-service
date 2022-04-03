package com.production.packager.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.production.packager.model.Produce;
import com.production.packager.repository.ProduceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.json.JSONObject;

@Component
public class PackageService {
    @Autowired
    ProduceRepository produceRepository;

    private static final Logger logger = LoggerFactory.getLogger(PackageService.class);

    @Scheduled(cron = "0 */1 * * * *")
    public void cronJobSch() {
        List<Produce> produces = produceRepository.findByPackaged(false);

        for(Produce produce : produces) {
            produce.setPackaged(true);
            logger.info("Produce with ProduceID - {} and OrderID - {} packaged", produce.getId(), produce.getOrderId());
            produceRepository.save(produce);
            CompleteOrder(produce.getOrderId());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        logger.info("Cron job completed at {}", strDate);
    }

    public void CompleteOrder(long orderId) {
        String completeOrderURL = System.getenv("ORDER_SERVICE_URL") + "/api/v1/orders/complete";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject orderJsonObject = new JSONObject();
        orderJsonObject.put("orderId", orderId);

        HttpEntity<String> request = 
            new HttpEntity<String>(orderJsonObject.toString(), headers);
        
        String orderResultAsJsonStr = 
        restTemplate.postForObject(completeOrderURL, request, String.class);

        logger.info(orderResultAsJsonStr);
    }
}
