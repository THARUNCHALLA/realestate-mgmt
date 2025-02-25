package com.Realestate.RealEstateManagement.Controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public class WelcomeController {

    public String welcome() {
        return "welcome";
    }
}
