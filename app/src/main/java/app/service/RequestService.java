package app.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.dto.BusinessTo;
import app.dto.DenominationTo;
import app.dto.RequestTo;
import app.dto.UserContext;
import app.enums.ReqStatus;
import app.model.Business;
import app.model.Denomination;
import app.model.Request;
import app.model.Shop;
import app.model.User;
import app.repository.RequestRepository;
import app.repository.ShopRepository;
import app.repository.UserRepository;
import app.utils.AppUtils;
import app.utils.CustomException;

@Service
public class RequestService {

	private RequestRepository reqRepo;
	private UserRepository userRepo;
	private ShopRepository shopRepo;

	@Autowired
	public RequestService(RequestRepository repo, UserRepository userRepo, ShopRepository shopRepo) {
		this.reqRepo = repo;
		this.userRepo = userRepo;
		this.shopRepo = shopRepo;
	}

	public RequestTo createRequest(RequestTo to) throws CustomException {

		Long userId = AppUtils.getLoggedInUser().getId();

		User user = userRepo.getReferenceById(userId);

		Shop shop = shopRepo.findById(to.getShopId()).orElseThrow(() -> new CustomException("Shop details not found"));

		Request request = new Request();
		request.setRequestDate(new Date(to.getRequestDate().getTime()));
		request.setCreatedBy(user);
		request.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		request.setShop(shop);

		for (DenominationTo deTo : to.getDenominations()) {

			Denomination deno = new Denomination();
			deno.setNote(deTo.getNote());
			deno.setCount(deTo.getCount());
			deno.setRequest(request);
			deno.setCreatedBy(user);
			deno.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			request.getDenominations().add(deno);
		}

		request = reqRepo.save(request);
		return extractRequest(request);
	}

	public RequestTo extractRequest(Request req) throws CustomException {

		RequestTo to = new RequestTo();
		to.setId(req.getId());
		to.setRequestDate(req.getRequestDate());
		to.setStatus(req.getStatus().name());

		Shop shop = shopRepo.findById(to.getId()).orElseThrow(() -> new CustomException("Shop details not found"));

		to.setShopId(shop.getId());

		Set<Denomination> denominations = reqRepo.fetchReqDenomination(to.getId());

		for (Denomination deno : denominations) {

			DenominationTo denoTo = new DenominationTo();
			denoTo.setId(deno.getId());
			denoTo.setNote(deno.getNote());
			denoTo.setCount(deno.getCount());
			denoTo.setReqId(to.getId());
			to.getDenominations().add(denoTo);
		}

		to.setCreatedBy(req.getCreatedBy().getUsername());
		to.setCreatedAt(req.getCreatedAt());

		if (req.getUpdatedBy() != null) {

			to.setUpdatedBy(req.getUpdatedBy().getUsername());
			to.setUpdatedAt(req.getUpdatedAt());
		}

		return to;
	}

	public RequestTo updateRequest(RequestTo to) throws CustomException {
		Request request = reqRepo.findById(to.getId()).orElseThrow(() -> new CustomException("Request is not found"));

		Long userId = AppUtils.getLoggedInUser().getId();

		User user = userRepo.getReferenceById(userId);

		Integer id = reqRepo.validate(request.getId(), user.getId());

		if (id == null) {
			throw new CustomException("You can only manage requests belonging to your business");
		}

		request.setUpdatedBy(user);
		request.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		request.setRequestDate(new Date(to.getRequestDate().getTime()));

		for (DenominationTo denoTo : to.getDenominations()) {

			Denomination deno = null;

			if (denoTo.getId() != null) {
				deno = reqRepo.findDenomination(denoTo.getId());
			} else {
				deno = new Denomination();
			}

			deno.setNote(denoTo.getNote());
			deno.setCount(denoTo.getCount());

			request.getDenominations().add(deno);

		}

		request = reqRepo.save(request);

		return extractRequest(request);
	}

	public void completeRequest(Long id) throws CustomException {

		Request request = reqRepo.findById(id).orElseThrow(() -> new CustomException("Request is not found"));

		request.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		request.setStatus(ReqStatus.COMPLETED);

		reqRepo.save(request);

	}

	public RequestTo viewRequest(Long id) throws CustomException {

		Request request = reqRepo.findById(id).orElseThrow(() -> new CustomException("Request Not Found"));

		UserContext loggedInUser = AppUtils.getLoggedInUser();

		Integer existid = reqRepo.validate(request.getId(), loggedInUser.getId());

		boolean isAdmin = loggedInUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));

		if (existid == null && !isAdmin) {
			throw new CustomException("Access denied. You are not authorized to view this Request data.");
		}

		return extractRequest(request);
	}

}
