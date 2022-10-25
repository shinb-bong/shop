package com.shop.common.config;

import com.shop.entity.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    MemberService memberService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // HttpServletRequest를 사용하여 Security 처리를 수행할 대상을 정해줌
        http.authorizeRequests()
                .mvcMatchers("/","/member/**","/item/**",
                        "/images/**").permitAll() // 모두 처리
                .mvcMatchers("/admin/**").hasRole("ADMIN") // 어드민만
                .anyRequest().authenticated();// 그 외 링크는 인증된 사람만

        // 권한에 맞지 않는 사용자가 리소스에 접근할때 수행되는 핸들러
        http.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint());

        // 첫 form 로그인 기본 설정 정보
        http.formLogin()
                .loginPage("/member/login")
                .defaultSuccessUrl("/")
                .usernameParameter("email") // default = "username"
                .failureUrl("/member/login/fail")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                .logoutSuccessUrl("/");
    }

    /**
     * static 디렉터리 하위 파일은 인증을 무시하도록 설정
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**","/js/**","/img/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
