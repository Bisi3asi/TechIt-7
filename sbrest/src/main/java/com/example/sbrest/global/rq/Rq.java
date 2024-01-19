package com.example.sbrest.global.rq;

import java.time.LocalDateTime;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.example.sbrest.domain.users.entity.Users;
import com.example.sbrest.global.security.SecurityUser;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Component
@RequestScope
@RequiredArgsConstructor
@Getter
@Setter
public class Rq {
	private final HttpServletRequest req;
	private final HttpServletResponse resp;
	private final EntityManager entityManager;
	private Users user;
	private SecurityUser securityUser;

	public boolean isLogin(){
		return getSecurityUser() != null;
	}
	public Users getMember(){
		if (!isLogin()) return null;

		if (user == null){
			user = entityManager.find(Users.class, getSecurityUser().getId());
		}
		return user;
	}

	public void setAccessTokenToCookie(String accessToken) {
		Cookie cookie = new Cookie("accessToken", accessToken);
		cookie.setPath("/");
		resp.addCookie(cookie);
	}

	public void setRefreshTokenToCookie(String refreshToken) {
		Cookie cookie = new Cookie("refreshToken", refreshToken);
		cookie.setPath("/");
		resp.addCookie(cookie);
	}

	public String getAccessTokenFromCookie(String defaultValue) {
		Cookie[] cookies = req.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("accessToken".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return defaultValue;
	}

	public String getRefreshTokenFromCookie(String defaultValue) {
		Cookie[] cookies = req.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("refreshToken".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return defaultValue;
	}

	public void removeAccessTokenFromCookie() {
		Cookie cookie = new Cookie("accessToken", "");
		cookie.setPath("/");
		cookie.setMaxAge(0);

		resp.addCookie(cookie);
	}

	public void removeRefreshTokenFromCookie() {
		Cookie cookie = new Cookie("refreshToken", "");
		cookie.setPath("/");
		cookie.setMaxAge(0);

		resp.addCookie(cookie);
	}

	public void setAuthentication(SecurityUser user) {
		Authentication auth = new UsernamePasswordAuthenticationToken(
			user,
			user.getPassword(),
			user.getAuthorities()
		);
		// Security 상에서 getContext.setAuthentication으로 Principal 사용 가능(권한 부여)
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
}
