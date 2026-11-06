package sw2.lab5.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Login con formulario propio
        http.formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/processlogin")
                .defaultSuccessUrl("/redirectByRol", true);

        // Logout (POST /logout con token CSRF). Con Spring Session la cookie se llama SESSION
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/post")
                .deleteCookies("SESSION")
                .invalidateHttpSession(true);

        // Permisos por rol
        http.authorizeRequests()
                .antMatchers("/user/list").hasAuthority("admin")
                .antMatchers("/user/**", "/redirectByRol").hasAnyAuthority("admin", "user")
                .antMatchers("/post/create", "/post/edit", "/post/guardar", "/post/borrar").hasAnyAuthority("admin", "user")
                .antMatchers("/amistad", "/amistad/**").hasAnyAuthority("admin", "user")
                .anyRequest().permitAll(); // /post (listar), /loginForm, css, js

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery("SELECT email, password, active FROM user WHERE email = ?");
        manager.setAuthoritiesByUsernameQuery("SELECT u.email, r.role_name FROM user u " +
                "INNER JOIN role r ON u.id_role = r.id_role " +
                "WHERE u.email = ? AND u.active = 1");
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
