package com.dragontho.aqtakehome.controllers;

import com.dragontho.aqtakehome.api.BinanceApiImpl;
import com.dragontho.aqtakehome.api.HuobiApiImpl;
import com.dragontho.aqtakehome.data.BinanceTicker;
import com.dragontho.aqtakehome.data.HuobiTicker;
import com.dragontho.aqtakehome.data.HuobiTickerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private BinanceApiImpl binanceApi;

    @Autowired
    private HuobiApiImpl huobiApi;

    @GetMapping("/binance")
    public BinanceTicker[] getBinanceTickers() {
        return binanceApi.getTickers().block();
    }

    @GetMapping("/huobi")
    public HuobiTickerResponse getHuobiTickers() {
        return huobiApi.getTickers().block();
    }
}
