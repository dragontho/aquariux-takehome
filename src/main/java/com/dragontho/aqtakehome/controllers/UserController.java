package com.dragontho.aqtakehome.controllers;

import com.dragontho.aqtakehome.data.internapi.TransactionDto;
import com.dragontho.aqtakehome.data.internapi.WalletDtoPage;
import com.dragontho.aqtakehome.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// For all requests in this controller, a header `token` is required for all requests for authorisation purposes
@RestController
@RequestMapping("/user/{username}")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping()
    public TransactionDto performTrade(@RequestHeader String token,
                                       @PathVariable String username,
                                       @RequestBody TransactionDto transactionDto) throws Exception {
        // Assuming token has been provided and is properly authorised, perform validation here
        if (!validateToken(username, token)) {
            throw new Exception("User not validated");
        }
        return userService.executeTrade(transactionDto);
    }

    @GetMapping("/wallets")
    public WalletDtoPage getWallets(@RequestHeader String token,
                                    @PathVariable String username,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) throws Exception {
        // Assuming token has been provided and is properly authorised, perform validation here
        if (!validateToken(username, token)) {
            throw new Exception("User not validated");
        }
        // Page is 0 indexed
        return userService.getWallets(username, page, size);
    }

    // Dummy method for validating identity
    private boolean validateToken(String userId, String token) {
        return true;
    }
}
