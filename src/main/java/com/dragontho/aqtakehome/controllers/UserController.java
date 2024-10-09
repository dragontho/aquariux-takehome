package com.dragontho.aqtakehome.controllers;

import com.dragontho.aqtakehome.data.internapi.TransactionDto;
import com.dragontho.aqtakehome.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/{username}")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping()
    public TransactionDto performTrade(@PathVariable String username,
                                       @RequestBody TransactionDto transactionDto,
                                       @RequestHeader String token) throws Exception {
        // Assuming token has been provided and is properly authorised, perform validation here
        if (!validateToken(username, token)) {
            throw new Exception("User not validated");
        }
        return userService.executeTrade(transactionDto);

    }

    private boolean validateToken(String userId, String token) {
        return true;
    }
}
