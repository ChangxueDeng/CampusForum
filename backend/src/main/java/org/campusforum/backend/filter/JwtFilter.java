package org.campusforum.backend.filter;


import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.campusforum.backend.utils.Const;
import org.campusforum.backend.utils.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Resource
    JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String headToken = request.getHeader(Const.HEAD_TOKEN);
        if (headToken != null && !jwtUtils.checkJwtTokenExpired(headToken)) {
            //进行jwt校验
            DecodedJWT decodedJwt = jwtUtils.decodedJwtToken(headToken);
            //将token内容转换为User
            User user = (User) jwtUtils.getUserDetails(decodedJwt);
            //获取Id
            String id = jwtUtils.getId(decodedJwt);
            //创建一个新SecurityContext,避免多线程争抢
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            //创建一个Authentication
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
            //设置详细请求信息
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            //放入SecurityContext
            context.setAuthentication(authenticationToken);
            //设置setContext
            SecurityContextHolder.setContext(context);
            request.setAttribute(Const.USER_ID, id);
        }
        filterChain.doFilter(request, response);
    }
}
