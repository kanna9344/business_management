package app.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.model.Denomination;
import app.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {

	@Query(value = "Select deno from Denomination deno where deno.request.id=:id")
	public Set<Denomination> fetchReqDenomination(@Param(value = "id") Long id);

	@Query(value = "Select deno from Denomination deno where deno.id=:id")
	public Denomination findDenomination(@Param(value = "id") Long id);

	@Query(value = "Select req.id from Request req where req.id=:id and req.shop.business.owner.id=:user")
	public Integer validate(@Param(value = "id") Long id, @Param(value = "user") Long user);
}
