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
import datadog.trace.api.Trace;
import datadog.trace.api.DDTags;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import io.opentracing.tag.Tags;

@Component
public class PackageService {
    @Autowired
    ProduceRepository produceRepository;

    private static final Logger logger = LoggerFactory.getLogger(PackageService.class);

    @Trace(operationName = "cronjob.execute", resourceName = "PackageService.cronJobSch")
    @Scheduled(cron = "0 */1 * * * *")
    public void cronJobSch() {
        List<Produce> produces = produceRepository.findByPackagedAndPackageTriesLessThanEqual(false, 3);

        for(Produce produce : produces) {
            Tracer tracer = GlobalTracer.get();
            Span span = tracer.buildSpan("cronjob.execute")
            .withTag(DDTags.SERVICE_NAME, "package-service")
            .withTag(DDTags.RESOURCE_NAME, "PackageService.cronJobSch")
            .start();
            logger.debug("SpanID - {}", span.context().toSpanId());
            try (Scope scope = tracer.activateSpan(span)) {
                span.setTag("order_id", produce.getOrderId());
                logger.debug("OrderID set as span tag");
                
                if(produce.getOrderId()%3==0) {
                    produce.setPackageTries(produce.getPackageTries()+1);
                    produceRepository.save(produce);
                    span.setTag(Tags.ERROR, true);
                    throw new IllegalArgumentException("Order ID cannot be a multiple of 3 for no reason");
                }

                produce.setPackaged(true);
                logger.info("Produce with ProduceID - {} and OrderID - {} packaged", produce.getId(), produce.getOrderId());
                produceRepository.save(produce);
                CompleteOrder(produce.getOrderId());
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
            } catch (Exception e) {
                logger.error("Internal server error with span tags");
            } finally {
                // Close span in a finally block
                span.finish();
            }
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
