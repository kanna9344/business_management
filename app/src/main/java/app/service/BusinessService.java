package app.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import app.dto.BusinessTo;
import app.dto.UserContext;
import app.dto.UserTo;
import app.model.Business;
import app.model.Role;
import app.model.User;
import app.repository.BusinessRepository;
import app.repository.UserRepository;
import app.utils.AppUtils;
import app.utils.CustomException;

@Service
public class BusinessService {

	private BusinessRepository repository;
	private UserService userService;
	private PasswordEncoder encoder;
	private UserRepository userRepo;

	@Autowired
	public BusinessService(BusinessRepository repo, UserService userService, PasswordEncoder encoder,
			UserRepository userRepo) {
		this.repository = repo;
		this.userService = userService;
		this.encoder = encoder;
		this.userRepo = userRepo;
	}

	public BusinessTo createBusiness(BusinessTo to) throws CustomException {

		Business business = new Business();
		business.setBusinessName(to.getBusinessName());
		business.setOwnerName(to.getOwnerName());
		business.setOwnerAddress(to.getOwnerAddress());

		Long userId = AppUtils.getLoggedInUser().getId();

		User user = userRepo.getReferenceById(userId);
		business.setCreatedBy(user);
		business.setCreatedAt(new Timestamp(System.currentTimeMillis()));

		User newOwner = new User();
		newOwner.setUsername(business.getOwnerName());

		String password = business.getOwnerName() + "@#$77";
		newOwner.setPassword(encoder.encode(password));

		Role role = new Role();
		role.setRole("BUSINESS");
		newOwner.getRoles().add(role);

		userService.handleDuplication(to.getUsername());

		userRepo.save(newOwner);

		business.setOwner(newOwner);

		business = repository.save(business);

		newOwner.setPassword(password);

		return extractBusiness(business, newOwner);

	}

	public BusinessTo updateBusiness(BusinessTo to) throws CustomException {

		Business business = repository.findById(to.getId())
				.orElseThrow(() -> new CustomException("Business Not Found"));
		Long userId = AppUtils.getLoggedInUser().getId();

		User user = userRepo.getReferenceById(userId);

		Integer busId = repository.validate(business.getId(), userId);

		if (busId == null) {
			throw new CustomException("You can only manage your own business");
		}

		business.setBusinessName(to.getBusinessName());
		business.setOwnerName(to.getOwnerName());
		business.setOwnerAddress(to.getOwnerAddress());
		business.setUpdatedBy(user);
		business.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

		business = repository.save(business);

		return extractBusiness(business, null);
	}

	public BusinessTo viewBusiness(Long id) throws CustomException {

		Business business = repository.findById(id).orElseThrow(() -> new CustomException("Business Not Found"));

		User save = business.getOwner();
		UserContext loggedInUser = AppUtils.getLoggedInUser();

		boolean isAdmin = loggedInUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));

		if (!save.getId().equals(loggedInUser.getId()) && !isAdmin) {
			throw new CustomException("Access denied. You are not authorized to view this Business data.");
		}

		return extractBusiness(business, null);
	}

	public BusinessTo extractBusiness(Business business, User user) {
		BusinessTo to = new BusinessTo();
		to.setId(business.getId());
		to.setBusinessName(business.getBusinessName());
		to.setOwnerName(business.getOwnerName());
		to.setOwnerAddress(business.getOwnerAddress());

		if (user != null) {
			to.setUsername(user.getUsername());
			to.setPassword(user.getPassword());

			for (Role role : user.getRoles()) {
				to.getRoles().add(role.getRole());
			}
		}

		to.setCreatedBy(business.getCreatedBy().getUsername());
		to.setCreatedAt(business.getCreatedAt());

		if (business.getUpdatedBy() != null) {

			to.setUpdatedBy(business.getUpdatedBy().getUsername());
			to.setUpdatedAt(business.getUpdatedAt());
		}

		return to;
	}

}
