package com.aftia.aem.soap.it.services;

import javax.jws.WebService;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aftia.aem.soap.core.annotations.CXFServiceInterface;
import com.aftia.aem.soap.core.services.CXFService;

@WebService
@Component(service = CXFService.class)
@CXFServiceInterface(wsdlInterface = GreeterService.class, address = "http://0.0.0.0:4504/soap/endpoint/greeter")
public class GreeterServiceImpl implements CXFService, GreeterService {

    private Logger log = LoggerFactory.getLogger(getClass());
    
    static final String NAME = GreeterService.class.getName();

    @Override
    public String sayHi(String message) {
        log.info(message);
        return "Hello";
    }
}
