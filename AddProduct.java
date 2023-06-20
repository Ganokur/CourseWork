package sample;

import java.sql.Date;
import java.time.LocalDate;

public class AddProduct {
    int quantity;
    double price;
    String name, category, manufacturer, unit;
    Date date;

    public AddProduct(String name, String category, String manufacturer, double price, int quantity, String unit, Date date) {
        this.quantity = quantity;
        this.price = price;
        this.name = name;
        this.category = category;
        this.manufacturer = manufacturer;
        this.unit = unit;
        this.date = date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
