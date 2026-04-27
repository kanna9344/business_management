package app.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import app.dto.UserContext;

public class AppUtils {

	public static UserContext getLoggedInUser() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserContext user = (UserContext) auth.getPrincipal();
		return user;
	}
}
