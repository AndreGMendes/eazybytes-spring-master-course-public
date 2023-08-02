package com.eazybytes.eazyschool.security;

import com.eazybytes.eazyschool.model.Person;
import com.eazybytes.eazyschool.model.Roles;
import com.eazybytes.eazyschool.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("!prod")
public class EazySchoolNonProdAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * 1 - Checks if the object 'authentication' is of the same type as the 'UsernamePasswordAuthenticationToken.class'
     * */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    /**
     * 2 - Validates business logic and returns the specific 'UsernamePasswordAuthenticationToken'
     * */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String email = authentication.getName();

        Person person = personRepository.readByEmail(email);

        if(
                null != person &&
                        person.getPersonId() > 0) {

            return new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    getGrantedAuthorities(person.getRoles()));

        }else{
            throw new BadCredentialsException("Invalid credentials!");
        }
    }

    /** 3 - Stores the role in an ArrayList 'grantedAuthorities'
     * which is returned inside the specific person 'UsernamePasswordAuthenticationToken'
     * by invoking the 'authenticate' method*/
    private List<GrantedAuthority> getGrantedAuthorities(Roles roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));
        return grantedAuthorities;
    }


}
