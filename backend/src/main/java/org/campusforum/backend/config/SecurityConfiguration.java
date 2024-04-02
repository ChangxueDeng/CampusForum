package org.campusforum.backend.config;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.campusforum.backend.entity.Result;
import org.campusforum.backend.entity.dto.AccountDTO;
import org.campusforum.backend.entity.vo.response.AuthorizeVO;
import org.campusforum.backend.filter.JwtFilter;
import org.campusforum.backend.filter.LogRequestFilter;
import org.campusforum.backend.service.AccountService;
import org.campusforum.backend.utils.Const;
import org.campusforum.backend.utils.JwtUtils;
import org.campusforum.backend.utils.StatusUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.Date;

/**
 * @author ChangxueDeng
 * Security配置
 */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Resource
    AccountService accountServiceImpl;

    @Resource
    JwtUtils jwtUtils;
    @Resource
    JwtFilter jwtFilter;
    @Resource
    LogRequestFilter logRequestFilter;


    /**
     * Security过滤器链
     * @param security 用于配置http安全规则
     * @return {@link SecurityFilterChain}
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security
                //访问权限
                .authorizeHttpRequests(conf -> {
                    conf.requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll();
                    conf.anyRequest().authenticated();
                })
                //csrf
                .csrf(AbstractHttpConfigurer::disable)
                //登陆
                .formLogin(conf -> {
                    conf.loginProcessingUrl("/api/auth/login");
                    conf.successHandler(loginSuccessHandler());
                    conf.failureHandler(loginFailureHandler());

                })
                //退出登陆
                .logout(conf ->{
                    conf.logoutUrl("/api/auth/logout");
                    conf.logoutSuccessHandler(logoutSuccessHandler());
                })
                //启用无状态
                .sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(conf -> {
                    //无权限
                    conf.accessDeniedHandler(new AccessDeniedHandler() {
                        @Override
                        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                            response.setCharacterEncoding("UTF-8");
                            response.setContentType("application/json");
                            response.getWriter().write(Result.failure(StatusUtils.STATUS_FORBIDDEN, StatusUtils.MESSAGE_FAILURE_FORBIDDEN).toJsonString());

                        }
                    });
                    //未认证
                    conf.authenticationEntryPoint(new AuthenticationEntryPoint() {
                        @Override
                        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                            response.setCharacterEncoding("UTF-8");
                            response.setContentType("application/json");
                            response.getWriter().write(Result.failure(StatusUtils.STATUS_UNAUTHORIZED, StatusUtils.MESSAGE_FAILURE_UNAUTHORIZED).toJsonString());
                        }
                    });
                })
                //添加过滤器
                .addFilterBefore(logRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, LogRequestFilter.class)
                .build();
    }

    private AuthenticationSuccessHandler loginSuccessHandler(){
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                AccountDTO accountDTO = accountServiceImpl.coverDTO(accountServiceImpl.findAccountByUsernameOrEmail(user.getUsername()));
                AuthorizeVO authorizeVO = new AuthorizeVO();
                BeanUtils.copyProperties(accountDTO, authorizeVO);
                //创建jwt令牌
                String token = jwtUtils.createJwtToken(user, accountDTO.getUsername(), accountDTO.getId());
                Date expire = jwtUtils.createExpireTime();
                authorizeVO.setToken(token);
                authorizeVO.setExpire(expire);
                response.getWriter().write(Result.success(authorizeVO,"登录成功").toJsonString());
            }
        };
    }
    private AuthenticationFailureHandler loginFailureHandler(){
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request,
                                                HttpServletResponse response,
                                                AuthenticationException exception) throws IOException, ServletException {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(Result.
                        failure(StatusUtils.STATUS_BAD_REQUEST, "登陆失败，用户名或密码存在错误").toJsonString());
            }
        };
    }
    private LogoutSuccessHandler logoutSuccessHandler(){
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                //获取token
                String token = request.getHeader(Const.HEAD_TOKEN);
                //将token加入黑名单
                jwtUtils.invalidateJwtToken(token);
                response.getWriter().write(Result.success("退出登陆成功").toJsonString());
            }
        };
    }


}
