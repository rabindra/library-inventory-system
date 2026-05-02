package com.rabs.libraryevents.consumer.service;


import com.rabs.libraryevents.consumer.entity.FailureRecord;
import com.rabs.libraryevents.consumer.repository.FailureRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FailureRecordService {

    private FailureRecordRepository failureRecordRepository;

    public FailureRecordService(FailureRecordRepository failureRecordRepository) {
        this.failureRecordRepository = failureRecordRepository;
    }

    public void saveFailureRecord(ConsumerRecord<Integer,String> consumerRecord, Exception ex, String status) {
        FailureRecord failureRecord = FailureRecord.builder()
                .topicName(consumerRecord.topic())
                .key((Integer) consumerRecord.key())
                .errorRecord(consumerRecord.value())
                .exception(ex.getCause().getMessage())
                .offsetVal(consumerRecord.offset())
                .partition(consumerRecord.partition())
                .status(status)
                .build();
        this.failureRecordRepository.save(failureRecord);
    }

    public List<FailureRecord> fetchFailedRecord(String retry) {
        return this.failureRecordRepository.findAllByStatus(retry);
    }
}
