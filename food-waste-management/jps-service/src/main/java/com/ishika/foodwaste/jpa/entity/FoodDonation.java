package com.ishika.foodwaste.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ishika.foodwaste.jpa.enums.DonationStatus;
@NamedQueries({
    @NamedQuery(
        name = "FoodDonation.findByLocation",
        query = "SELECT f FROM FoodDonation f WHERE f.location = :location"
    ),
//    @NamedQuery(
//        name = "FoodDonation.findByAssignedTo",
//        query = "SELECT f FROM FoodDonation f WHERE f.assignedTo = :aid"
//    ),
    @NamedQuery(
            name = "FoodDonation.countByLocation",
            query = "SELECT f.location, COUNT(f) FROM FoodDonation f GROUP BY f.location"
        ),
    @NamedQuery(
    	    name = "FoodDonation.findRecentByDonor",
    	    query = "SELECT f FROM FoodDonation f WHERE f.donor.id = :donorId ORDER BY f.date DESC"
    	),
    @NamedQuery(
    	    name = "FoodDonation.findTopDonorsThisMonth",
    	    query = "SELECT new  com.ishika.foodwaste.jpa.utils.TopDonorDTO(f.donor.name, f.donor.email, COUNT(f)) " +
    	            "FROM FoodDonation f " +
    	            "WHERE MONTH(f.date) = MONTH(CURRENT_DATE) AND YEAR(f.date) = YEAR(CURRENT_DATE) " +
    	            "GROUP BY f.donor.name, f.donor.email " +
    	            "ORDER BY COUNT(f) DESC"
    	),
    @NamedQuery(
    	    name = "FoodDonation.countByStatus",
    	    query = "SELECT f.status, COUNT(f) FROM FoodDonation f GROUP BY f.status"
    	),
    @NamedQuery(
    	    name = "FoodDonation.findAcceptedDeliveriesByDonor",
    	    query = "SELECT f FROM FoodDonation f " +
    	            "WHERE f.donor.id = :donorId " +
    	            "AND f.status = com.ishika.foodwaste.jpa.enums.DonationStatus.ACCEPTED "
    	)


})

@Entity
@Table(name = "food_donations")
  public class FoodDonation {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
      private int fid;
    private String name;

    private String email;

    private String food;

    private String type;

    private String category;

    private String quantity;

    private LocalDateTime date;

    private String address;

    private String location;

    private String phoneno;
    
    @Enumerated(EnumType.STRING)
    private DonationStatus status = DonationStatus.PENDING;

    public DonationStatus getStatus() {
		return status;
	}


	public void setStatus(DonationStatus status) {
		this.status = status;
	}
	@Lob
    @JsonIgnore
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;

    public byte[] getImage() {
		return image;
	}


	public void setImage(byte[] image) {
		this.image = image;
	}
	@Column(nullable = true)
	private Integer price;

	public Integer getPrice() {
		return price;
	}


	public void setPrice(Integer price) {
		this.price = price;
	}

	@ManyToOne
    @JoinColumn(name = "assigned_to")
    private NGOS assignedNGO;

    @ManyToOne
    @JoinColumn(name = "delivery_by")
    private DeliveryPerson deliveryPerson;

    
    @ManyToOne
    @JoinColumn(name = "donor_id")  // foreign key in food_donations table
    private Donor donor;


	public int getFid() {
		return fid;
	}


	public void setFid(int fid) {
		this.fid = fid;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getFood() {
		return food;
	}


	public void setFood(String food) {
		this.food = food;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	public String getQuantity() {
		return quantity;
	}


	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}


	public LocalDateTime getDate() {
		return date;
	}


	public void setDate(LocalDateTime date) {
		this.date = date;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getPhoneno() {
		return phoneno;
	}


	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}


	public NGOS getAssignedNGO() {
		return assignedNGO;
	}


	public void setAssignedNGO(NGOS assignedNGO) {
		this.assignedNGO = assignedNGO;
	}


	public DeliveryPerson getDeliveryPerson() {
		return deliveryPerson;
	}


	public void setDeliveryPerson(DeliveryPerson deliveryPerson) {
		this.deliveryPerson = deliveryPerson;
	}


	public Donor getDonor() {
		return donor;
	}


	public void setDonor(Donor donor) {
		this.donor = donor;
	}

    // Getters and Setters

    
}

