package com.example.userbalanceapi.ui.model.response;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

@Getter
@Setter
public class UserBalanceResponse {

    private String id;
    private Double balance;
    private String currency = "RUB";

    public UserBalanceResponse(String id, Double balance) {
        this.id = id;
        this.balance = balance;
    }

    public void convertBalance(String currency) {
        boolean isCurrencyWrong = false;
        currency = currency.toLowerCase(Locale.ROOT);

        try {
            URL url = new URL("https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/rub.json");

            Scanner in = new Scanner((InputStream) url.getContent());
            StringBuilder result = new StringBuilder();
            while (in.hasNext()) {
                result.append(in.nextLine());
            }

            JSONObject object = new JSONObject(result.toString());
            JSONObject fromRubToCurrent = object.getJSONObject("rub");
            System.out.println(fromRubToCurrent.get(currency));
            if (fromRubToCurrent.keySet().contains(currency)) {
                this.balance *= fromRubToCurrent.getDouble(currency);
                this.currency = currency;
            }
            else {
                isCurrencyWrong = true;
            }

        } catch (Exception e) {
            isCurrencyWrong = true;
        }

        if (isCurrencyWrong) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error: Currency is wrong");
        }
    }
}
