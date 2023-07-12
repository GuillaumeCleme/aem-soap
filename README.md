# AEM SOAP Services

This project contains a deployable library that enables the creation and publishing of OSGI-based SOAP services with automatic WSDL generation via the use of Apache CXF.

## Modules

The main parts of the template are:

* core: Java bundle containing all core functionality like OSGi services, listeners or schedulers, as well as component-related Java code such as servlets or request filters.
* it.tests: Java based integration tests
* ui.config: contains runmode specific OSGi configs for the project
* all: a single content package that embeds all of the compiled modules (bundles and content packages) including any vendor dependencies

## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

To build all the modules and deploy the `all` package to a local instance of AEM, run in the project root directory the following command:

    mvn clean install -PautoInstallSinglePackage

Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallSinglePackagePublish

Or alternatively

    mvn clean install -PautoInstallSinglePackage -Daem.port=4503

Or to deploy only the bundles (`core` and `it.tests`) to the author, run

    mvn clean install -PautoInstallBundle

Or to deploy only a single content package, run in the sub-module directory (i.e `ui.config`)

    mvn clean install -PautoInstallPackage

## Testing

There are three levels of testing contained in the project:

### Unit tests

This show-cases classic unit testing of the code contained in the bundle. To
test, execute:

    mvn clean test

### Integration tests

This allows running integration tests that exercise the capabilities of AEM via
HTTP calls to its API. To run the integration tests, run (note, bundles must be deployed first):

    mvn clean verify -Plocal

Test classes must be saved in the `src/main/java` directory (or any of its
subdirectories), and must be contained in files matching the pattern `*IT.java`.

The configuration provides sensible defaults for a typical local installation of
AEM. If you want to point the integration tests to different AEM author and
publish instances, you can use the following system properties via Maven's `-D`
flag.

| Property | Description | Default value |
| --- | --- | --- |
| `it.author.url` | URL of the author instance | `http://localhost:4502` |
| `it.author.user` | Admin user for the author instance | `admin` |
| `it.author.password` | Password of the admin user for the author instance | `admin` |
| `it.publish.url` | URL of the publish instance | `http://localhost:4503` |
| `it.publish.user` | Admin user for the publish instance | `admin` |
| `it.publish.password` | Password of the admin user for the publish instance | `admin` |

The integration tests in this archetype use the [AEM Testing
Clients](https://github.com/adobe/aem-testing-clients) and showcase some
recommended [best
practices](https://github.com/adobe/aem-testing-clients/wiki/Best-practices) to
be put in use when writing integration tests for AEM.

## Maven settings

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html

## Creating WSDLs

Creating and registering WSDLs can be done by annotating classes via both OSGI annotations and CXF annotations that are exposed by the `core` module of this project.

### Adding the Core bundle as a dependency

To start, add the `core` bundle as a dependency to your project:

```
<dependency>
    <groupId>com.aftia.aem</groupId>
    <artifactId>aem-soap.core</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Annotate your service

Once you've added the dependency to you own module, you can start annotating your OSGI service with CXF annotations.

You will need a new interface to define your service, e.g.:

```
package com.aftia.aem.soap.it.services;

import javax.jws.WebService;

@WebService
public interface GreeterService {
    public String sayHi(String message);
}
```

This interface is ready to be registed as a web service via the use of the `@WebService` annotations. A sample is available here: `./it.tests/src/main/java/com/aftia/aem/soap/it/services/GreeterService.java`

With an interface created, you can now create your implementation class, e.g.:

```
package com.aftia.aem.soap.it.services;

import javax.jws.WebService;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aftia.aem.soap.core.annotations.CXFServiceInterface;
import com.aftia.aem.soap.core.services.CXFService;

@WebService
@Component(service = CXFService.class)
@CXFServiceInterface(wsdlInterface = GreeterService.class)
public class GreeterServiceImpl implements CXFService, GreeterService {

    private Logger log = LoggerFactory.getLogger(getClass());
    
    static final String NAME = GreeterService.class.getName();

    @Override
    public String sayHi(String message) {
        log.info(message);
        return "Hello";
    }
}
```

This class contains a few more annotations. A sample is available here: `./it.tests/src/main/java/com/aftia/aem/soap/it/services/GreeterServiceImpl.java` The `@WebService` annotation is still present to register the class as a web service. The `@Component` annotation is also present to register this implementation class as an OSGI service. 

Pay close attention to the fact that this class now also implements the `CXFService` interface and registers the `@Component` with `(service = CXFService.class)`. 

With this, we also have a `@CXFServiceInterface(wsdlInterface = GreeterService.class)` annotation that provides additional information to our CXF activator by letting the service know which interface defines the service.

Once completed, deploy your new OSGI bundle to the target AEM server along with the `all` package containing the registration mechanisms, and configurations.

### Testing the WSDL

Once your service is deployed, and all bundles are Active, your WSDL will be accessible at the following URL: `http://<server>:4504/soap/endpoint?wsdl`

## Manual Deployment

The dependencies required to enable SOAP support are all included in the `all` package that can be deployed to an AEM instance via Package Manager, but if for some reason one wishes to deploy bundles manually, the following combination can be used to enable SOAP support:

* [osgi.cmpn](https://mvnrepository.com/artifact/org.osgi/osgi.cmpn/4.3.1)
* [cxf-dosgi-ri-singlebundle-distribution](https://mvnrepository.com/artifact/org.apache.cxf.dosgi/cxf-dosgi-ri-singlebundle-distribution/1.3.1)
* aem-soap.core

## Known Limitations

At the moment, this module allows the creation, publishing, and invocation of SOAP services in an AEM/OSGI context. It does not however fully support the following features at the moment:

* Authentication
* SSL
* Apache CXF Interceptors & additional features

### Tested Environments

This module has successfully been tested on AEM Forms `6.5.10` - `6.5.15`.