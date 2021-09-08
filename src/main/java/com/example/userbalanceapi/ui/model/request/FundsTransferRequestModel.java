package com.example.userbalanceapi.ui.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FundsTransferRequestModel {

    private String userIdToTransfer;
    private Double balance;
}
