package org.campusforum.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.campusforum.backend.utils.Const;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 跨域请求过滤器
 * @author ChangxueDeng
 */
@Component
@Order(Const.ORDER_FILTER_CORS)
public class DoCorsFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.cors(response);
        chain.doFilter(request, response);
    }

    private void cors(HttpServletResponse response) throws ServletException, IOException {
        //允许发起跨域请求的源
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        //允许发起跨域请求的方法
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
        //允许跨域请求提交哪些 Header
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
