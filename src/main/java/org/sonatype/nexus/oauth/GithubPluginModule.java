package org.sonatype.nexus.oauth;

import javax.inject.Named;

import org.sonatype.nexus.plugin.NexusPluginPropertiesModule;

import com.google.inject.AbstractModule;

@Named
public class GithubPluginModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new NexusPluginPropertiesModule("nexus-oauth"));
  }
}
