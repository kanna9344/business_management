package app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.model.Business;
import app.model.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {

	@Query(value = "Select shop.id from Shop shop where shop.id=:id and shop.business.owner.id=:user")
	public Integer validate(@Param(value = "id") Long id, @Param(value = "user") Long user);

	@Query(value = "Select shop.business from Shop shop where shop.id=:id")
	public Business findBusiness(@Param(value = "id") Long id);
	
	

}
