package si.fri.prpo.seminarska.api.v1;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import si.fri.prpo.seminarska.api.v1.viri.CORSFilter;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;

@DeclareRoles({"user", "admin"})
@OpenAPIDefinition(info = @Info(title = "Baza clanov ksk", version = "v1",
        contact = @Contact(email = "maksbertoncelj@gmail.com"),
license = @License(name = "dev"), description = "API za prpo seminarsko"),
servers = @Server(url = "http://localhost:9090/"))

@ApplicationPath("v1")
public class SeznamClanovApplication extends ResourceConfig {
    public SeznamClanovApplication() {

        packages("si.fri.prpo.seminarska.api.v1"); // Adjust the package name to your project
        register(MultiPartFeature.class);  // Register Multipart support
        register(CORSFilter.class); // Register the CORS filter
    }

}
