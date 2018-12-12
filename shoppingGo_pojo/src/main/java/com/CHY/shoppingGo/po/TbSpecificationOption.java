package com.CHY.shoppingGo.po;

import java.io.Serializable;
import java.util.Objects;

public class TbSpecificationOption implements Serializable {
    private Long id;

    private String optionName;

    private Long specId;

    private Integer orders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName == null ? null : optionName.trim();
    }

    public Long getSpecId() {
        return specId;
    }

    public void setSpecId(Long specId) {
        this.specId = specId;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbSpecificationOption that = (TbSpecificationOption) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(optionName, that.optionName) &&
                Objects.equals(specId, that.specId) &&
                Objects.equals(orders, that.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, optionName, specId, orders);
    }
}