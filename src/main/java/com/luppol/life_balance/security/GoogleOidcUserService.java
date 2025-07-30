package com.luppol.life_balance.security;

import com.luppol.life_balance.models.Person;
import com.luppol.life_balance.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
    private final PersonRepository personRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest req) throws OAuth2AuthenticationException {
        OidcUser googleUser = new OidcUserService().loadUser(req);

        String email = googleUser.getEmail();
        Person person = personRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->  // Registration use case
                        new UsernameNotFoundException("No user found: " + email));

        AuthUser authUser = new AuthUser(person.getId(), email);

        return new DefaultOidcUser(authUser.getAuthorities(),
                googleUser.getIdToken(),
                googleUser.getUserInfo(),
                "email");
    }
}
