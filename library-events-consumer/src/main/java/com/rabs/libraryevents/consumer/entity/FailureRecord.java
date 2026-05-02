package com.rabs.libraryevents.consumer.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FailureRecord {
    @Id
    @GeneratedValue
    private Integer id;

    private String topicName;

    @Column(name = "failure_key") // "key" is a reserved word
    private Integer key;
    private String errorRecord;

    @Column(name = "partition_num") // "partition" is a reserved word
    private Integer partition;
    private String status;
    private Long offsetVal;
    private String exception;

}
