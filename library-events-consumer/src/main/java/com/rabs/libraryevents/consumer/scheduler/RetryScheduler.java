package com.rabs.libraryevents.consumer.scheduler;

import com.rabs.libraryevents.consumer.entity.FailureRecord;
import com.rabs.libraryevents.consumer.service.FailureRecordService;
import com.rabs.libraryevents.consumer.service.LibraryEventsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RetryScheduler {

    private FailureRecordService failureRecordService;
    private LibraryEventsService libraryEventsService;

    public  RetryScheduler(FailureRecordService failureRecordService, LibraryEventsService libraryEventsService) {
        this.failureRecordService = failureRecordService;
        this.libraryEventsService = libraryEventsService;
    }

    @Scheduled(fixedRate = 10000)
    public void retryFailedRecord(){
        List<FailureRecord> failureRecordList = failureRecordService.fetchFailedRecord("RETRY");

        failureRecordList.forEach(record -> {
            var consumerRecord = buildConsumerRecord(record);

            try{
                libraryEventsService.processLibraryEvent(consumerRecord);
                record.setStatus("SUCCESS");
            }catch (Exception e){
                log.error("Exception in retryFailedRecords: {} ", e.getMessage(), e);
            }
        });
    }

    private ConsumerRecord<Integer, String> buildConsumerRecord(FailureRecord record) {

        return new ConsumerRecord<>(
                record.getTopicName(),
                record.getPartition(),
                record.getOffsetVal(),
                record.getKey(),
                record.getErrorRecord()
        );
    }
}
