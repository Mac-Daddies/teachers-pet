package com.tiy;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Created by jessicatracy on 10/10/16.
 */
@org.springframework.stereotype.Controller
public class Controller {

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home(Model model, HttpSession session) {
//        model.addAttribute("loggedIn", false);
        if (session.getAttribute("teacherWhoIsLoggedIn") == null) {
            model.addAttribute("teacherWhoIsLoggedIn", false);
        } else {
            model.addAttribute("teacherWhoIsLoggedIn", session.getAttribute("teacherWhoIsLoggedIn"));
        }

        return "home";
    }

    @RequestMapping(path = "/classList", method = RequestMethod.GET)
    public String classList(int teacherId, Model model) {
        model.addAttribute("teacherId", teacherId);
        return "classList";
    }

//    @RequestMapping(path = "/backToHome", method = RequestMethod.GET)
//    public String backToHome(int teacherId, Model model) {
//        model.addAttribute("teacherId", teacherId);
//        return "home";
//    }

//    @RequestMapping(path = "/backToHome", method = RequestMethod.POST)
//    public String backToHome(@RequestBody Teacher teacher, Model model) {
//        model.addAttribute("teacherWhoIsLoggedIn", teacher);
//        return "home";
//    }

    @RequestMapping(path = "/gradebook", method = RequestMethod.GET)
    public String gradebook(int courseId,Model model){
        model.addAttribute("courseId", courseId);
        return "gradebook";
    }

//    @RequestMapping(path = "/gradebook", method = RequestMethod.POST)
//    public String gradebook(@RequestBody Course course, Model model, HttpSession session){
//        model.addAttribute("course", course);
////        session.setAttribute("currentCourse", course);
//        return "gradebook";
//    }

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
