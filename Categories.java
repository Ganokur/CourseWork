package sample;

public class Categories {
    int NumberOfProductsNames, NumberOfGoods;
    double AveragePrice, MaxPrice, MinPrice;
    String CategoryName;

    public Categories(String categoryName, int numberOfProductsNames, int numberOfGoods, double averagePrice, double maxPrice, double minPrice) {
        NumberOfProductsNames = numberOfProductsNames;
        NumberOfGoods = numberOfGoods;
        AveragePrice = averagePrice;
        MaxPrice = maxPrice;
        MinPrice = minPrice;
        CategoryName = categoryName;
    }

    public int getNumberOfProductsNames() {
        return NumberOfProductsNames;
    }

    public void setNumberOfProductsNames(int numberOfProductsNames) {
        NumberOfProductsNames = numberOfProductsNames;
    }

    public int getNumberOfGoods() {
        return NumberOfGoods;
    }

    public void setNumberOfGoods(int numberOfGoods) {
        NumberOfGoods = numberOfGoods;
    }

    public double getAveragePrice() {
        return AveragePrice;
    }

    public void setAveragePrice(double averagePrice) {
        AveragePrice = averagePrice;
    }

    public double getMaxPrice() {
        return MaxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        MaxPrice = maxPrice;
    }

    public double getMinPrice() {
        return MinPrice;
    }

    public void setMinPrice(double minPrice) {
        MinPrice = minPrice;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
}