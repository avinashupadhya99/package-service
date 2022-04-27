package com.production.packager.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name="production_produce")
public class Produce {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
      name = "sequence-generator",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @Parameter(name = "sequence_name", value = "produce_sequence"),
        @Parameter(name = "initial_value", value = "483597632"),
        @Parameter(name = "increment_size", value = "4")
        }
    )
    private Long id;
    @NotNull(message = "OrderID is mandatory")
    private Long orderId;
    @NotNull(message = "Produce Quantity is mandatory")
    private Long quantity;
    @NotBlank(message = "Produce Product is mandatory")
    private String product;
    @Column(columnDefinition="boolean default false")
    private boolean packaged;
    @Column
    private int packageTries = 0;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
    
    public String getProduct() {
        return this.product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public boolean getPackaged() {
        return this.packaged;
    }

    public void setPackaged(boolean packaged) {
        this.packaged = packaged;
    }

    public int getPackageTries() {
        return this.packageTries;
    }

    public void setPackageTries(int packageTries) {
        this.packageTries = packageTries;
    }

}
