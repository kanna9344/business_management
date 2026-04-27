package app.dto;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import app.markers.Create;
import app.markers.Update;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class RequestTo {

	@NotNull(groups = Update.class, message = "Id is required")
	private Long id;

	@NotNull(groups = { Create.class, Update.class }, message = "Request date is required")
	private Date requestDate;

	private String status;

	@NotNull(groups = Create.class, message = "Shop details is required")
	private Long shopId;

	@NotEmpty(groups = { Create.class, Update.class }, message = "Denomination is required")
	private Set<@Valid DenominationTo> denominations = new HashSet<DenominationTo>();

	private String createdBy;
	private Timestamp createdAt;
	private String updatedBy;
	private Timestamp updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Set<DenominationTo> getDenominations() {
		return denominations;
	}

	public void setDenominations(Set<DenominationTo> denominations) {
		this.denominations = denominations;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}
