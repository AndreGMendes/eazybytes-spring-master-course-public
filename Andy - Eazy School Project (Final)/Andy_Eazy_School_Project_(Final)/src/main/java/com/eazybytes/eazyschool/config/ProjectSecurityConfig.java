package com.eazybytes.eazyschool.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectSecurityConfig {

    /**
     /home
     /holidays
     /contact
     /saveMsg
     /courses
     /about
     */

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf()
                .ignoringRequestMatchers("/saveMsg")
                .ignoringRequestMatchers("/public/**")
                .ignoringRequestMatchers("/api/**")
                .ignoringRequestMatchers("/data-api/**")
                .ignoringRequestMatchers("/eazyschool/actuator/**")

            .and()
                .authorizeHttpRequests()
                .requestMatchers("/dashboard").authenticated()
                .requestMatchers("/displayMessages/**").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .requestMatchers("/closeMsg/**").hasRole("ADMIN")
                .requestMatchers("/displayProfile").authenticated()
                .requestMatchers("/updateProfile").authenticated()
                .requestMatchers("/student/**").hasRole("STUDENT")
                .requestMatchers("", "/", "/home").permitAll()
                .requestMatchers("/holidays/**").permitAll()
                .requestMatchers("/contact").permitAll()
                .requestMatchers("/saveMsg").permitAll()
                .requestMatchers("/courses").permitAll()
                .requestMatchers("/about").permitAll()
                .requestMatchers("/assets/**").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/logout").permitAll()
                .requestMatchers("/public/**").permitAll()

                .requestMatchers("/data-api/**").authenticated()
                .requestMatchers("/eazyschool/actuator/**").hasRole("ADMIN")

            .and()
                .formLogin().loginPage("/login")
                .defaultSuccessUrl("/dashboard")
                .failureUrl("/login?error=true").permitAll()

            .and()
                .logout().logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true).permitAll()

            .and()
                .httpBasic();

        return http.build();

    }



    /**Password encoding*/
    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }


}
