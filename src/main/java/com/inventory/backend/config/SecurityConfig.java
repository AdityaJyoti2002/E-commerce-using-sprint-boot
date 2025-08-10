package com.inventory.backend.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                    .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/orders/**").authenticated()
                        .requestMatchers("/order/**").authenticated()
                        .requestMatchers("/cart/**").authenticated()
                        .requestMatchers("/return-order/**").authenticated()
                        .requestMatchers("/razorpay/payment").authenticated()  // Allow anyone to access /razorpay/payment
                        .anyRequest().permitAll()  // Allow all other requests
                    )
                    .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/login?error=true")
                        .permitAll()
                    )
                    .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                    )
                    .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/razorpay/payment")  // Exclude CSRF for /razorpay/payment
                    )
                    .build();
    }

    @Bean
    JdbcUserDetailsManager users(DataSource dataSource, PasswordEncoder passwordEncoder) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        
        // Add a default admin user if it doesn't exist
      UserDetails adminUser = new User(
                "admin",
                passwordEncoder.encode("admin"),
                AuthorityUtils.createAuthorityList("ROLE_ADMIN")
            );

        if (!jdbcUserDetailsManager.userExists(adminUser.getUsername())) {
            jdbcUserDetailsManager.createUser(adminUser);
        }
        return jdbcUserDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
