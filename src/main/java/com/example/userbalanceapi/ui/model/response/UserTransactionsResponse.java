package com.example.userbalanceapi.ui.model.response;

import com.example.userbalanceapi.io.entity.TransactionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserTransactionsResponse {

    private Double currentBalance;
    private List<TransactionEntity> transactionHistory;
}
