package app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.model.Business;

public interface BusinessRepository extends JpaRepository<Business, Long> {

	@Query("SELECT business.id FROM Business business WHERE business.id = :id AND business.owner.id = :user")
	public Integer validate(@Param("id") Long id, @Param("user") Long user);

	@Query("Select business from Business business where business.id=:id and business.createdBy.id=:user")
	public Business viewBusiness(@Param(value = "id") Long id, @Param(value = "user") Long user);

}
