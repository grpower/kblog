package com.kzk.kblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping(value="/index.html")
    private String index(){
        return "index";
    }
    
}
