package vrd.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;

@Configuration
class AuthenticationConfiguration extends
        GlobalAuthenticationConfigurerAdapter {

    @Autowired
    UserService userDetailsServices;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {

        auth
                .userDetailsService(userDetailsServices);

    }


}
