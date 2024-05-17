package com.remiges.rigel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.remiges.rigel.dto.RequestDTO;
import com.remiges.rigel.dto.RigelConfigDTO;
import com.remiges.rigel.service.RigelService;

@RestController
public class RigelController {

    @Autowired
    private RigelService etcdInteractionService;

    @GetMapping("/rigel/fetchConfigValue")
    public String fetchConfigValue(@RequestBody RigelConfigDTO rigelConfigDTO) {

        return etcdInteractionService.fetchConfigValue(rigelConfigDTO.getEnvironment(), rigelConfigDTO.getKey());

    }
    
    @PostMapping("/rigel/put")
	public String putValue(@RequestBody RequestDTO request) {
		try {
			etcdInteractionService.putValue(request.getKey(), request.getValue());
			return "Data has been inserted in etcd";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error: Failed to put value into etcd.";
		}
	}

}


