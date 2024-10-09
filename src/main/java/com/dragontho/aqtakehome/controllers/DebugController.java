package com.dragontho.aqtakehome.controllers;

import com.dragontho.aqtakehome.api.BinanceApiClient;
import com.dragontho.aqtakehome.api.HuobiApiClient;
import com.dragontho.aqtakehome.data.externapi.BinanceTicker;
import com.dragontho.aqtakehome.data.externapi.HuobiTickerResponse;
import com.dragontho.aqtakehome.models.User;
import com.dragontho.aqtakehome.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private BinanceApiClient binanceApi;

    @Autowired
    private HuobiApiClient huobiApi;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/binance")
    public BinanceTicker[] getBinanceTickers() {
        return binanceApi.getTickers().block();
    }

    @GetMapping("/huobi")
    public HuobiTickerResponse getHuobiTickers() {
        return huobiApi.getTickers().block();
    }

    @GetMapping("/getUser")
    public User getUser() {
        User users = userRepository.findByUsername("user").get();
        System.out.println("Users found: " + users);
        return users;
    }
}
