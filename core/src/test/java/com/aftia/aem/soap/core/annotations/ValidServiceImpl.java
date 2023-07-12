package com.aftia.aem.soap.core.annotations;

@CXFServiceInterface(wsdlInterface = ValidService.class, address = "")
public class ValidServiceImpl implements ValidService{
    @Override
    public String getMessage() {
        return "Hello";
    }
}
