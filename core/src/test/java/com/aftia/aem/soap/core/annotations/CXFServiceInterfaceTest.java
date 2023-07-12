package com.aftia.aem.soap.core.annotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class CXFServiceInterfaceTest {
    
    @Test
    public void testValidAnnotation() {
        ValidService validService = new ValidServiceImpl();
        CXFServiceInterface serviceInterface = validService.getClass().getAnnotation(CXFServiceInterface.class);

        assertNotNull(serviceInterface);
        assertEquals(ValidService.class, serviceInterface.wsdlInterface());
    }

    @Test
    public void testInvalidAnnotation() {
        ValidService invalidService = new InvalidServiceImpl();

        assertNull(invalidService.getClass().getAnnotation(CXFServiceInterface.class));
    }
}
