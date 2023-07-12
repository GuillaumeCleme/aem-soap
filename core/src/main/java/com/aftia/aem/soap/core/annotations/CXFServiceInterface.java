package com.aftia.aem.soap.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define the interface describing a class 
 * to be exposed as a SOAP endpoint via Apache CXF.
 * <p>
 * wsdlInterface (required): Declares the interface which defines this endpoint
 * address (required): The address at which the service will be made available remotely
 * exportedInterfaces: This is a comma-separated list of fully qualified Java interfaces that should be made available remotely. Use * to list all registered interfaces (default: *).
 * exportedConfigs: Specifies the mechanism for configuring the service exposure (default: org.apache.cxf.ws).
 * frontend: The CXF frontend which will be used to create endpoints (default: simple).
 * databinding: The CXF databindings which will be used to marshall objects (default: aegis).
 * <p>
 * <b>Example:</b> {@code @CXFServiceInterface(wsdlInterface = GreeterService.class)}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CXFServiceInterface {
    public Class<?> wsdlInterface();
    public String address();
    public String exportedInterfaces() default "*";
    public String exportedConfigs() default "org.apache.cxf.ws";
    public String frontend() default "simple";
    public String databinding() default "aegis";
}
