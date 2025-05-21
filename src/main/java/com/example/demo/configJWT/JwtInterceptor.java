package com.example.demo.configJWT;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    public JwtInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    //ham xu ly request truoc khi den controller, false chan request, true tiep tuc xu ly
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Nếu là preflight request (OPTIONS) thì cho qua -> lỗi về CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        //lay token tu request header được đính kèm tại các api trên frontend
        String token = request.getHeader("Authorization");
        System.out.println("Token sau khi request.getHeader() tại prehandle: " + token);
        //Xử lý sai định dạng token từ header Authorization
        //token có dạng như sau: (Authorization: Bearer eyJhbGciOiJIUzI1NiIsIn...)
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access Denied ! Co the sai dinh dang token");
            return false;
        }

        token = token.substring(7); // Bỏ "Bearer " de tra ve dung token thuc su

        //Xử lý sai validate tu JwtUtils như: chữ ký sai, token hết hạn,...
        if (!jwtUtils.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token: signature is incorrect or has expired.");
            return false;
        }

        //Phân quyền theo role phai la 'admin' moi duoc truy cap
        String requestURI = request.getRequestURI();
        String role = jwtUtils.getRoleFromToken(token);
        if (("/employees/list".equals(requestURI) && !"admin".equals(role))
//                ||("/employees/102".equals(requestURI) && !"user".equals(role))
        ) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Wrong role, Access Denied!");
            return false;
        }

        //lấy thông tin từ token gán vào request, để các controller có thể sử dụng.
        request.setAttribute("username", jwtUtils.getUsernameFromToken(token));
        request.setAttribute("role", jwtUtils.getRoleFromToken(token));
        request.setAttribute("employeeId", jwtUtils.getEmployeeIdFromToken(token));
        System.out.println("EmployeeId nhận được sau khi giải mã token tại prehandle là: "+jwtUtils.getEmployeeIdFromToken(token));
        // tra ve reponse neu thanh cong
//        response.setStatus(HttpServletResponse.SC_OK);
        return true;//Nếu token hợp lệ, request tiếp tục được xử lý.

    }
}

