package polytech.aisw.eom.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatMultipartConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private final int maxPartCount;

    public TomcatMultipartConfig(@Value("${app.media.max-part-count:50}") int maxPartCount) {
        this.maxPartCount = maxPartCount;
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(this::customizeConnector);
    }

    private void customizeConnector(Connector connector) {
        connector.setMaxPartCount(maxPartCount);
    }
}
