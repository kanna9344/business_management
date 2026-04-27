package app.dto;

import java.util.HashSet;
import java.util.Set;

import app.markers.Create;
import app.markers.Verify;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserTo {

	private Long id;

	@NotBlank(message = "Username is required", groups = { Create.class, Verify.class })
	private String username;

	@NotBlank(message = "Password is required", groups = { Create.class, Verify.class })
	@Size(min = 12, message = "Minimum 12 characters required", groups = Create.class)
	private String password;

	@NotEmpty(message = "Roles is required", groups = Create.class)
	private Set<String> roles = new HashSet<String>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

}
