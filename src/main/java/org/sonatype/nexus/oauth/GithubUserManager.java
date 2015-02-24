package org.sonatype.nexus.oauth;

import java.util.Set;

import javax.inject.Named;

import org.sonatype.security.usermanagement.AbstractReadOnlyUserManager;
import org.sonatype.security.usermanagement.DefaultUser;
import org.sonatype.security.usermanagement.User;
import org.sonatype.security.usermanagement.UserNotFoundException;
import org.sonatype.security.usermanagement.UserSearchCriteria;
import org.sonatype.security.usermanagement.UserStatus;

@Named("Github")
public class GithubUserManager extends AbstractReadOnlyUserManager {

  @Override
  public String getSource() {
    return "Github";
  }

  @Override
  public String getAuthenticationRealmName() {
    return "GithubRealm";
  }

  @Override
  public Set<User> listUsers() {
    return null;
  }

  @Override
  public Set<String> listUserIds() {
    return null;
  }

  @Override
  public Set<User> searchUsers(UserSearchCriteria criteria) {
    return null;
  }

  @Override
  public User getUser(String userId) throws UserNotFoundException {
    User user = new DefaultUser();
    user.setEmailAddress( "jason@maven.org" );
    user.setUserId( userId );
    user.setSource( getSource() );
    user.setStatus( UserStatus.active );
    return user;
  }
}
