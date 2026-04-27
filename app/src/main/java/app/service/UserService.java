package app.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import app.dto.UserContext;
import app.dto.UserTo;
import app.model.Role;
import app.model.User;
import app.repository.RoleRepository;
import app.repository.UserRepository;
import app.utils.CustomException;

@Service
public class UserService implements UserDetailsService {

	private UserRepository repository;
	private RoleRepository roleRepo;

	public UserService(UserRepository repo, RoleRepository roleRepo) {
		this.repository = repo;
		this.roleRepo = roleRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = repository.findUser(username);

		if (user == null) {
			throw new UsernameNotFoundException("User not found: " + username);
		}

		List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getRole())).toList();

		return new UserContext(user.getId(), user.getUsername(), user.getPassword(), authorities);

	}

	public void handleDuplication(String username) throws CustomException {
		User existUser = repository.findUser(username);

		if (existUser != null && existUser.getUsername().equalsIgnoreCase(username)) {
			throw new CustomException("Username Already Exist");
		}
	}

	public void createUser(UserTo to) throws CustomException {

		handleDuplication(to.getUsername());
		User user = new User();

		user.setUsername(to.getUsername());
		user.setPassword(to.getPassword());
		Role role = null;

		for (String data : to.getRoles()) {

			Role existRole = roleRepo.findByRole(data);

			if (existRole != null) {
				role = existRole;
			} else {
				role = new Role();
				role.setRole(data);
			}
			user.getRoles().add(role);
		}

		repository.save(user);

	}

}
