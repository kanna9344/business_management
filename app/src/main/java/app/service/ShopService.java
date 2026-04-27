package app.service;

import java.sql.Timestamp;

import org.springframework.stereotype.Service;

import app.dto.BusinessTo;
import app.dto.ShopTo;
import app.dto.UserContext;
import app.model.Business;
import app.model.Shop;
import app.model.User;
import app.repository.BusinessRepository;
import app.repository.ShopRepository;
import app.repository.UserRepository;
import app.utils.AppUtils;
import app.utils.CustomException;

@Service
public class ShopService {

	private ShopRepository shopRepo;
	private BusinessRepository businessRepo;
	private UserRepository userRepo;

	public ShopService(ShopRepository shopRepo, BusinessRepository businessRepo, UserRepository userRepo) {
		this.shopRepo = shopRepo;
		this.businessRepo = businessRepo;
		this.userRepo = userRepo;
	}

	public ShopTo saveShop(ShopTo to) throws CustomException {

		Business business = this.businessRepo.findById(to.getBusId())
				.orElseThrow(() -> new CustomException("Business not found"));

		Shop shop = new Shop();
		shop.setBusiness(business);
		shop.setShopName(to.getShopName());

		Long userId = AppUtils.getLoggedInUser().getId();

		User save = userRepo.getReferenceById(userId);

		shop.setCreatedBy(save);
		shop.setCreatedAt(new Timestamp(System.currentTimeMillis()));

		Shop result = shopRepo.save(shop);
		return extractShop(result);
	}

	public ShopTo updateShop(ShopTo to) throws CustomException {

		Shop shop = shopRepo.findById(to.getId()).orElseThrow(() -> new CustomException("Shop is not found"));

		Long userId = AppUtils.getLoggedInUser().getId();

		User save = userRepo.getReferenceById(userId);

		Integer id = shopRepo.validate(shop.getId(), save.getId());

		if (id == null) {
			throw new CustomException("You can only manage shops belonging to your business");
		}

		shop.setShopName(to.getShopName());
		shop.setUpdatedBy(save);
		shop.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

		Shop result = shopRepo.save(shop);

		return extractShop(result);
	}

	public ShopTo extractShop(Shop shop) throws CustomException {

		ShopTo to = new ShopTo();

		to.setId(shop.getId());
		to.setShopName(shop.getShopName());

		Business business = this.shopRepo.findBusiness(shop.getId());
		to.setBusId(business.getId());

		if (shop.getCreatedBy() != null) {
			to.setCreatedBy(shop.getCreatedBy().getUsername());
			to.setCreatedAt(shop.getCreatedAt());
		}

		if (shop.getUpdatedBy() != null) {

			to.setUpdatedBy(shop.getUpdatedBy().getUsername());
			to.setUpdatedAt(shop.getUpdatedAt());
		}
		return to;
	}

	public ShopTo viewShop(Long id) throws CustomException {

		Shop shop = shopRepo.findById(id).orElseThrow(() -> new CustomException("Shop Not Found"));
		UserContext loggedInUser = AppUtils.getLoggedInUser();
		Integer existid = shopRepo.validate(shop.getId(), loggedInUser.getId());
		boolean isAdmin = loggedInUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
		if (existid == null && !isAdmin) {
			throw new CustomException("Access denied. You are not authorized to view this Shop data.");
		}

		return extractShop(shop);
	}

}
