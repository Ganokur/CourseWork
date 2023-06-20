package sample;

public class Manufacturers {
    int id, NumberOfGoods;
    Double PercentageOfGoods;
    String ManufacturerName, Categories;

    public Manufacturers(int id, String manufacturerName, String categories,int numberOfGoods, Double percentageOfGoods) {
        this.id = id;
        NumberOfGoods = numberOfGoods;
        PercentageOfGoods = percentageOfGoods;
        ManufacturerName = manufacturerName;
        Categories = categories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumberOfGoods() {
        return NumberOfGoods;
    }

    public void setNumberOfGoods(int numberOfGoods) {
        NumberOfGoods = numberOfGoods;
    }

    public Double getPercentageOfGoods() {
        return PercentageOfGoods;
    }

    public void setPercentageOfGoods(Double percentageOfGoods) {
        PercentageOfGoods = percentageOfGoods;
    }

    public String getManufacturerName() {
        return ManufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        ManufacturerName = manufacturerName;
    }

    public String getCategories() {
        return Categories;
    }

    public void setCategories(String categories) {
        Categories = categories;
    }
}
