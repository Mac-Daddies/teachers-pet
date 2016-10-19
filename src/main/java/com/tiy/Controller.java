package com.tiy;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

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
    public String gradebook(int courseId,Model model){
        model.addAttribute("courseId", courseId);
        return "gradebook";
    }
    @RequestMapping(path = "/assignment", method = RequestMethod.GET)
    public String addAss(){
        return "addass";
    }
    @RequestMapping(path = "oldhome", method = RequestMethod.GET)
    public String oldhome(){
        return "oldhome";
    }
    @RequestMapping(path = "/graph", method = RequestMethod.GET)
    public String graph(int courseId,Model model){
        model.addAttribute("courseId", courseId);
        return "graph";
    }






}
