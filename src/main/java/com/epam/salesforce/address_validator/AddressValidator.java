package com.epam.salesforce.address_validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/")
@SpringBootApplication
public class AddressValidator {

    private static final Set<String> restrictedAddresses = new HashSet<>(3);
    static{
        restrictedAddresses.add("reject");
        restrictedAddresses.add("badAddress");
        restrictedAddresses.add("stop");
    }

    public static void main(String[] args) {
        SpringApplication.run(AddressValidator.class, args);
    }

    @RequestMapping(value = "/{address}", method = RequestMethod.GET)
    ResponseEntity<String> address(@PathVariable String address){
        if(restrictedAddresses.contains(address)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
