package com.example.springredditclone.config;

import com.example.springredditclone.security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Điều hướng khi xác thực bảo mật, giúp đưa vào đúng class ???
    // Xác đinh một bean cho AuthenticationManager và định cấu hình AuthenticationManagerBuilder để sử dụng giao diện UserDetailsService để kiểm tra TT người dùng??
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //Cấu hình Security
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable()   //Disable tính năng chống tấn công
                .authorizeRequests()
                .antMatchers("/api/auth/**")
                .permitAll()            //Cho phép tất cả mọi người truy cập vào địa chỉ “/api/auth/**” (Request không cần phải xác thực)
                .antMatchers(HttpMethod.GET, "/api/subreddit")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts/")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts/**")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/api/comments/**")
                .permitAll()
                .antMatchers("/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**")
                .permitAll()
                .anyRequest()
                .authenticated();  //Tất cả các request khác đều cần phải xác thực mới được truy cập

        //Để được thực thi trước JwtAuthenticationFilter
        //Add các filter vào ứng dụng của chúng ta, thứ mà sẽ hứng các request để xử lý trước khi tới các xử lý trong controllers.
        //https://viblo.asia/p/securing-spring-boot-with-jwt-part-2-xac-thuc-nguoi-dung-dua-tren-du-lieu-trong-co-so-du-lieu-63vKjnJVK2R
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    // Cấu hình AuthenticationManagerBuilder để sử dụng interface UserDetailsService để xác thực thông tin người dùng.
    // Trong SP Security có 1 object quan trong là UserDetailsService, nó nắm giữ tt quan trọng như :
    // username này là ai trong hệ thống, có quyền gì ...
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        // Gọi userDetailsService trong bước tiếp theo (UserDetailsServiceImpl)
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());    //Sử dụng PasswordEncoder mã hóa password người dùng
    }

    //PasswordEncoder để Spring Security sử dụng mã hóa mật khẩu người dùng
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
