package com.tiy;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by jessicatracy on 10/10/16.
 */
@org.springframework.stereotype.Controller
public class Controller {

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home() {
        return "home";
    }
    @RequestMapping(path = "/gradebook", method = RequestMethod.GET)
    public String gradebook(){
        return "gradebook";
    }
    @RequestMapping(path = "/assignment", method = RequestMethod.GET)
    public String addAss(){
        return "addass";
    }





}
