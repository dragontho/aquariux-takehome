package com.dragontho.aqtakehome.controllers;

import com.dragontho.aqtakehome.data.internapi.AggregatedPriceDto;
import com.dragontho.aqtakehome.services.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/prices")
public class PriceController {

    @Autowired
    private ExchangeService exchangeService;

    @GetMapping()
    public List<AggregatedPriceDto> getLatestAggregatedPrice(@RequestParam(required = false) String symbol) throws Exception {
        if (Objects.isNull(symbol)) {
            return exchangeService.getLatestAggregatedPrices();
        } else {
            return Collections.singletonList(exchangeService.getLatestAggregatedPrice(symbol));
        }
    }

}
