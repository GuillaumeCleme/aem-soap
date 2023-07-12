package com.aftia.aem.soap.core.activators;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aftia.aem.soap.core.annotations.CXFServiceInterface;
import com.aftia.aem.soap.core.services.CXFService;

/**
 * Apache CXF Activator that allows SOAP-based services to be exposed via OSGi.
 * 
 * @author Guillaume Clement
 * @see https://cxf.apache.org/distributed-osgi-greeter-demo-walkthrough.html
 */
@Component(service = BundleActivator.class,
           immediate = true,
           name = "Apache CXF Activator"
)
public class CXFActivator implements BundleActivator {

    private static BundleContext bundleContext;

    @Reference(	policy = ReferencePolicy.DYNAMIC, 
				cardinality = ReferenceCardinality.MULTIPLE,
				bind="bindService",
				unbind="unbindService")
	private volatile List<CXFService> cxfServices = new ArrayList<CXFService>();
	private volatile List<RegistrationHolder> registrationHolders = new ArrayList<RegistrationHolder>();

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void start(BundleContext context) throws Exception {
        log.info("Starting CXF Bundle");
        bundleContext = context;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        log.info("Stopping CXF Bundle");
    }

    protected synchronized void bindService(CXFService cxfService) throws Exception {
        CXFServiceInterface serviceInterface;
        RegistrationHolder holder = new RegistrationHolder();
        Hashtable<String, String> props = new Hashtable<String, String>();
        holder.setCxfService(cxfService);

        try {
            serviceInterface = cxfService.getClass().getAnnotation(CXFServiceInterface.class);

            if(null == serviceInterface){
                throw new NullPointerException("Service annotation is null.");
            }

            props.put("service.exported.interfaces", serviceInterface.exportedInterfaces());
            props.put("service.exported.configs", serviceInterface.exportedConfigs());
            props.put("org.apache.cxf.ws.address", serviceInterface.address());
            props.put("org.apache.cxf.ws.frontend", serviceInterface.frontend()); 
            props.put("org.apache.cxf.ws.databinding", serviceInterface.databinding());

            ServiceRegistration<?> registration = bundleContext.registerService(serviceInterface.wsdlInterface().getName(), cxfService, props);
            holder.setRegistration(registration);

            registrationHolders.add(holder);

            log.info("Bound service implementation: [{}] with {} total services.", cxfService.getClass().getName(), registrationHolders.size());
        } catch (NullPointerException e) {
            throw new Exception("Service registration is missing a valid @CXFServiceInterface annotation.", e);
        }
	}
	
	protected synchronized void unbindService(CXFService cxfService) {
        registrationHolders.stream()
            .filter(r -> r.getCxfService().equals(cxfService)).findFirst()
            .ifPresent(r -> {
                r.getRegistration().unregister();
                registrationHolders.remove(r);
            });

		log.info("Unbound service implementation: [" + cxfService.getClass().getName() + "].");
	}

    class RegistrationHolder {
        private CXFService cxfService;
        private ServiceRegistration<?> registration;

        public CXFService getCxfService() {
            return cxfService;
        }
        public void setCxfService(CXFService cxfService) {
            this.cxfService = cxfService;
        }
        public ServiceRegistration<?> getRegistration() {
            return registration;
        }
        public void setRegistration(ServiceRegistration<?> registration) {
            this.registration = registration;
        }
    }
}
