package cn.wildfirechat.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Slf4j
@RestController
public class WebsocketController {
    @RequestMapping("/wsIndex")
    public ModelAndView sendMessage(Map<String, Object> map) {
        return new ModelAndView("wsIndex", map);
    }
}