package com.dragontho.aqtakehome.controllers;

import com.dragontho.aqtakehome.data.internapi.AggregatedPrice;
import com.dragontho.aqtakehome.services.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/trade")
public class TradeController {

    @Autowired
    private ExchangeService exchangeService;

    @GetMapping("/prices")
    public List<AggregatedPrice> getLatestAggregatedPrice(@RequestParam(required = false) String symbol) throws Exception {
        if (Objects.isNull(symbol)) {
            return exchangeService.getLatestAggregatedPrices();
        } else {
            return Collections.singletonList(exchangeService.getLatestAggregatedPrice(symbol));
        }
    }

}
