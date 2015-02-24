package org.sonatype.nexus.oauth;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;

@Named("GithubRealm")
public class GithubRealm extends AuthenticatingRealm {

  private static final String API_SECRET = "${githubRealm.apiSecret}";
  private static final String AUTHENTICATION_CACHING = "${githubRealm.authenticationCaching}";
  private static final String PROTECTED_RESOURCE_URL = "https://api.github.com/";

  private String apiSecret;

  @Inject
  public GithubRealm(@Named(API_SECRET) String apiSecret, @Named(AUTHENTICATION_CACHING) boolean authenticationCaching) {
    this.apiSecret = apiSecret;
    if (authenticationCaching) {
      //
      // Shiro must having a caching manager configured and you should take a look at the following before turning it on:
      //
      // http://shiro.apache.org/static/1.2.1/apidocs/org/apache/shiro/realm/AuthenticatingRealm.html
      //
      setCachingEnabled(true);
    }
  }

  @Override
  public String getName() {
    return "GithubRealm";
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
    
    UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
    //
    // User name is password => 40 chars of random | base64 encoded user name
    //
    String username = new String(Base64.decode(new String(token.getPassword()).substring(39).getBytes()));
    //
    // Access token is the username base64 encoded
    //
    Token accessToken = new Token(new String(Base64.decode(token.getUsername().getBytes())), apiSecret);
    //
    // Create the OAuth request and send off to Github
    //
    OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
    request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());
    Response response = request.send();

    if (!response.isSuccessful()) {
      throw new AuthenticationException("The user " + token.getUsername() + " cannot be authenticated against Github.");
    }

    return new SimpleAuthenticationInfo(username, token.getPassword(), "GithubRealm");
  }

  @Override
  public boolean supports(AuthenticationToken token) {
    return UsernamePasswordToken.class.isAssignableFrom(token.getClass());
  }
}
