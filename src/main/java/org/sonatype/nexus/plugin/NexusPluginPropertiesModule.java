package org.sonatype.nexus.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.guice.bean.binders.ParameterKeys;

import com.google.inject.AbstractModule;

public class NexusPluginPropertiesModule extends AbstractModule {

  private final String fileName;

  public NexusPluginPropertiesModule(String id) {
    this.fileName = id + ".properties";
  }

  @Override
  protected void configure() {
    InjectableProperties properties = new InjectableProperties();
    //
    // This tells Guice that upon successful Injector creation to perform injection upon the InjectableProperties
    // instance that we created above. When that happens the loadProperties method below with the @Inject will
    // be invoked and cause the properties to be loaded. This will happen before the Injector can be used
    // to lookup any instances.
    //
    requestInjection(properties);
    bind(ParameterKeys.PROPERTIES).toInstance(properties);
  }

  private class InjectableProperties extends AbstractMap<String, String> {

    private Map<String, String> properties = Collections.emptyMap();

    @Inject
    public void loadProperties(@Named("${application-conf}") String configurationDirectory) {
      try {
        InputStream is = null;
        //
        // We'll look in the standard Nexus configuration directory first for our configuration resource
        //
        File file = new File(configurationDirectory, fileName).getCanonicalFile();
        if (file.exists()) {
          is = new FileInputStream(file);
        } else {
          is = getClass().getClassLoader().getResourceAsStream(fileName);
        }

        System.out.println(is);
        
        if (is != null) {
          Properties props = new Properties();
          props.load(is);
          properties = (Map) props;
          System.out.println(properties);
        }
      } catch (Exception e) {
      }
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
      return properties.entrySet();
    }
  }
}