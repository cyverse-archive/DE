package org.iplantc.de;

import org.iplantc.de.conf.WebMvcConfig;

import org.atmosphere.cpr.AtmosphereServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * The entry point for the DE webapp.
 *
 * @author jstroot
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application extends AbstractAnnotationConfigDispatcherServletInitializer
                         implements ServletContextInitializer {

    private Environment environment;
    final static Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException, URISyntaxException {
        for (String arg : args) {
            if (arg.equals("--version")) {
                Application instance = new Application();
                // Print manifest and exit
                instance.printVersion();
                System.exit(0);
                return;
            } else if (arg.startsWith("--spring")) {
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Unrecognized argument \"")
                  .append(arg)
                  .append("\". Try \"--version\"");
                System.out.println(sb.toString());
            }
        }
        SpringApplication.run(Application.class, args);

    }

    private void printVersion() throws IOException {
        Enumeration<URL> resources = getClass().getClassLoader()
                                               .getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            Manifest manifest = new Manifest(resources.nextElement().openStream());
            Attributes attr = manifest.getMainAttributes();

            // Make sure this is our manifest
            boolean isCorrectManifest = false;
            for (Object key : attr.keySet()) {
                if (key.toString().equals("Start-Class")) {
                    if (attr.get(key).toString().equals(getClass().getName())) {
                        isCorrectManifest = true;
                    }
                }
            }
            if (isCorrectManifest) {
                StringBuilder sb = new StringBuilder();
                for (Object key : attr.keySet()) {
                    sb.append(key)
                      .append(": ")
                      .append(attr.get(key).toString())
                      .append("\n");
                }
                System.out.println(sb.toString());
            }
        }
    }

    @Override
    public void onStartup(ServletContext container) {
        ServletRegistration.Dynamic dispatcher =
                container.addServlet("AtmosphereServlet", new AtmosphereServlet());
        dispatcher.setLoadOnStartup(0);
        dispatcher.addMapping("/de/websocket");
        dispatcher.setInitParameter("org.atmosphere.cpr.packages", "org.iplantc.de.server");
        LOG.error("************** Started AtmosphereServlet ***********");
    }


    @Override
    protected String[] getServletMappings() {
        String[] servletMappings = {"/", "/de", "/belphegor", "*.rpc"};
        return servletMappings;
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[0];
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        Class<?>[] servletConfigClasses = {WebMvcConfig.class};
        return servletConfigClasses;
    }
}
