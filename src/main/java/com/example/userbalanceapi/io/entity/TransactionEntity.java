package com.example.userbalanceapi.io.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue
    private Long transactionId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "transaction_amount", nullable = false)
    private Double transactionAmount;

    @Column(name = "transaction_date_time", nullable = false)
    private Timestamp transactionDateTime;

    @Column(name = "transaction_description")
    private String transactionDescription;

    public TransactionEntity(String userId, Double transactionAmount, Timestamp transactionDateTime, String transactionDescription) {
        this.userId = userId;
        this.transactionAmount = transactionAmount;
        this.transactionDateTime = transactionDateTime;
        this.transactionDescription = transactionDescription;
    }
}
