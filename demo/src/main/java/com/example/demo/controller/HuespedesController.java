package com.example.demo.controller;

import com.example.demo.repository.HuespedesRepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/huespedes")
@Controller
public class HuespedesController {

    @Autowired
    private HuespedesRepository repo;

    public HuespedesController(HuespedesRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/admin")
    public String huespedesAdmin(Model model) {
        model.addAttribute("huespedes", repo.findAll());
        return "huespedes-admin"; // -> huespedes-admin.html
    }

}