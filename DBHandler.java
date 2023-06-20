package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.Double.parseDouble;

public class DBHandler extends Configs {
    Connection dbConnection;

    public Connection getDbConnection()
            throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

        Class.forName("com.mysql.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

        return dbConnection;
    }


    public ResultSet getUser(String UserLogin, String UserPassword) {
        ResultSet resSet = null;

        String select = "SELECT * FROM " + Const.USER_TABLE + " WHERE " +
                Const.USERS_USERNAME + "=? AND " + Const.USERS_PASSWORD + "=?";


        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);

            prSt.setString(1, UserLogin);
            prSt.setString(2, UserPassword);

            resSet = prSt.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resSet;
    }


    public ObservableList<Product> getDataProducts() {
        ObservableList<Product> list = FXCollections.observableArrayList();
        try {
            PreparedStatement psDp = getDbConnection().prepareStatement("SELECT * FROM products");
            ResultSet rs = psDp.executeQuery();

            while (rs.next()) {
                list.add(new Product(Integer.parseInt(rs.getString("idProducts")), rs.getString("Name"), rs.getString("Category"), rs.getString("Manufacturer"), parseDouble(rs.getString("Price")), Integer.parseInt(rs.getString("Quantity")), rs.getString("Unit"), rs.getDate("Date")));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }


    public ObservableList<Categories> getDataCategories() {
        ObservableList<Categories> list = FXCollections.observableArrayList();
        try {
            PreparedStatement psDp = getDbConnection().prepareStatement("SELECT * FROM categories");
            ResultSet rs = psDp.executeQuery();

            while (rs.next()) {
                list.add(new Categories(rs.getString("CategoryName"), Integer.parseInt(rs.getString("NumberOfProductsNames")), Integer.parseInt(rs.getString("NumberOfGoods")), Double.parseDouble(rs.getString("AveragePrice")), Double.parseDouble(rs.getString("MaxPrice")), Double.parseDouble(rs.getString("MinPrice"))));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<Manufacturers> getDataManufacturers(String name) {
        ObservableList<Manufacturers> list = FXCollections.observableArrayList();

        try {
            PreparedStatement psDp = getDbConnection().prepareStatement("SELECT * FROM manufacturers WHERE " + Const.MANUFACTURERS_NAME + " LIKE '" + name + "'");
            ResultSet rs = psDp.executeQuery();

            while (rs.next()) {
                list.add(new Manufacturers(Integer.parseInt(rs.getString("idManufacturers")), rs.getString("ManufacturerName"), rs.getString("Categories"), Integer.parseInt(rs.getString("NumberOfGoods")), Double.parseDouble(rs.getString("PercentageOfGoods"))));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }


    public void Add_Products(AddProduct addproduct) {
        String insert = "INSERT INTO " + Const.PRODUCTS_TABLE + " (" +
                Const.PRODUCTS_NAME + "," + Const.PRODUCTS_CATEGORY + "," +
                Const.PRODUCTS_MANUFACTURER + "," + Const.PRODUCTS_PRICE + "," +
                Const.PRODUCTS_QUANTITY + "," + Const.PRODUCTS_UNIT + "," +
                Const.PRODUCTS_DATE + ")" +
                " VALUES(?,?,?,?,?,?,?)";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);

            prSt.setString(1, addproduct.getName());
            prSt.setString(2, addproduct.getCategory());
            prSt.setString(3, addproduct.getManufacturer());
            prSt.setDouble(4, addproduct.getPrice());
            prSt.setInt(5, addproduct.getQuantity());
            prSt.setString(6, addproduct.getUnit());
            prSt.setDate(7, addproduct.getDate());

            prSt.executeUpdate();

            String select_category = "SELECT * FROM products WHERE Category LIKE '" + addproduct.getCategory() + "'";
            Statement prSt_category = getDbConnection().createStatement();
            ResultSet rs_category = prSt_category.executeQuery(select_category);

            int count_category = 0;
            while (rs_category.next()) {
                count_category++;
            }

            String select_manufactury = "SELECT * FROM products WHERE Manufacturer LIKE '" + addproduct.getManufacturer() + "'";
            Statement prSt_manufactury = getDbConnection().createStatement();
            ResultSet rs_manufactury = prSt_manufactury.executeQuery(select_manufactury);

            int count_manufactury = 0;
            while (rs_manufactury.next()) {
                count_manufactury++;
            }

            if (count_category > 1 && count_manufactury > 1) {
                PreparedStatement prSt_select = getDbConnection().prepareStatement(select_category);
                ResultSet Rs = prSt_select.executeQuery();

                Update_Category(Rs);

                PreparedStatement prSt_select_second = getDbConnection().prepareStatement(select_manufactury);
                ResultSet RS = prSt_select_second.executeQuery();

                Update_Manufacturer(RS);
            } else if (count_category <= 1) {
                String insert_category = "INSERT INTO " + Const.CATEGORIES_TABLE + " (" +
                        Const.CATEGORIES_NAME + "," + Const.CATEGORIES_NUMBER_OF_PRODUCTS_NAME + "," +
                        Const.CATEGORIES_NUMBER_OF_GOODS + "," + Const.CATEGORIES_AVERAGE_PRICE + "," +
                        Const.CATEGORIES_MAX_PRICE + "," + Const.CATEGORIES_MIN_PRICE + ")" +
                        " VALUES(?,?,?,?,?,?)";

                PreparedStatement prSt_ins_categories = getDbConnection().prepareStatement(insert_category);
                prSt_ins_categories.setString(1, addproduct.getCategory());
                prSt_ins_categories.setInt(2, 1);
                prSt_ins_categories.setInt(3, addproduct.getQuantity());
                prSt_ins_categories.setDouble(4, addproduct.getPrice());
                prSt_ins_categories.setDouble(5, addproduct.getPrice());
                prSt_ins_categories.setDouble(6, addproduct.getPrice());

                prSt_ins_categories.executeUpdate();

                Add_Manufacturer(addproduct);

                PreparedStatement prSt_select_second = getDbConnection().prepareStatement(select_manufactury);
                ResultSet RS = prSt_select_second.executeQuery();

                Update_Manufacturer(RS);
            } else {
                String select = "SELECT * FROM products WHERE Category LIKE '" + addproduct.getCategory() + "'";
                PreparedStatement prSt_select = getDbConnection().prepareStatement(select);
                ResultSet Rs = prSt_select.executeQuery();
                if (Rs.next()) {
                    Rs.getString(1);
                }
                Add_Manufacturer(addproduct);
                Update_Category(Rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void Delete_Products(int id) {
        try {
            String delete = "DELETE FROM " + Const.PRODUCTS_TABLE + " WHERE " +
                    Const.PRODUCTS_ID + "=" + id;

            String select_product_manufacturer = "Select * FROM products WHERE idProducts = " + id;
            PreparedStatement PRST = getDbConnection().prepareStatement(select_product_manufacturer);
            ResultSet R_S = PRST.executeQuery();

            if (R_S.next()) {
                R_S.getString(1);
            }
            String select_category = "SELECT * FROM products WHERE Category LIKE '" + R_S.getString("Category") + "'";
            Statement prSt_category = getDbConnection().createStatement();
            ResultSet rs_category = prSt_category.executeQuery(select_category);

            int count_category = 0;
            while (rs_category.next()) {
                count_category++;
            }

            String select_manufactury = "SELECT * FROM products WHERE Manufacturer LIKE '" + R_S.getString("Manufacturer") + "'";
            Statement prSt_manufactury = getDbConnection().createStatement();
            ResultSet rs_manufactury = prSt_manufactury.executeQuery(select_manufactury);

            int count_manufactury = 0;
            while (rs_manufactury.next()) {
                count_manufactury++;
            }
            if (count_category > 1 && count_manufactury > 1) {

                PreparedStatement prSt1 = getDbConnection().prepareStatement(delete);
                prSt1.executeUpdate();

                PreparedStatement prSt_select = getDbConnection().prepareStatement(select_category);
                ResultSet Rs = prSt_select.executeQuery();

                Update_Category(Rs);

                PreparedStatement prSt_select_second = getDbConnection().prepareStatement(select_manufactury);
                ResultSet RS = prSt_select_second.executeQuery();

                Update_Manufacturer(RS);
            } else if (count_category <= 1) {
                PreparedStatement prSt = getDbConnection().prepareStatement(select_product_manufacturer);
                ResultSet rs = prSt.executeQuery();

                if (rs.next()) {
                    rs.getString(1);
                }
                String delete_category = "DELETE FROM " + Const.CATEGORIES_TABLE + " WHERE CategoryName LIKE '" + rs.getString("Category") + "'";
                PreparedStatement St_delete_category = getDbConnection().prepareStatement(delete_category);
                St_delete_category.executeUpdate();

                String delete_manufacturer = "DELETE FROM " + Const.MANUFACTURERS_TABLE + " WHERE ManufacturerName LIKE '" + rs.getString("Manufacturer") + "' AND " +
                        "Categories LIKE '" + rs.getString("Category") + "'";
                PreparedStatement St_delete_manufacturer = getDbConnection().prepareStatement(delete_manufacturer);
                St_delete_manufacturer.executeUpdate();

                PreparedStatement prSt_select_second = getDbConnection().prepareStatement(select_manufactury);
                ResultSet RS = prSt_select_second.executeQuery();

                Update_Manufacturer(RS);

                PreparedStatement prSt1 = getDbConnection().prepareStatement(delete);
                prSt1.executeUpdate();
            } else {
                PreparedStatement prSt = getDbConnection().prepareStatement(select_product_manufacturer);
                ResultSet rs = prSt.executeQuery();

                String delete_manufacturer = "DELETE FROM " + Const.MANUFACTURERS_TABLE + " WHERE ManufacturerName = '" + rs.getString("Manufacturer") + "'";
                PreparedStatement St_delete_manufacturer = getDbConnection().prepareStatement(delete_manufacturer);
                St_delete_manufacturer.executeUpdate();

                PreparedStatement prSt1 = getDbConnection().prepareStatement(delete);
                prSt1.executeUpdate();


                PreparedStatement prSt_select = getDbConnection().prepareStatement(select_category);
                ResultSet Rs = prSt_select.executeQuery();

                Update_Category(Rs);

                PreparedStatement prSt_select_second = getDbConnection().prepareStatement(select_manufactury);
                ResultSet RS = prSt_select_second.executeQuery();

                Update_Manufacturer(RS);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean select_id(int id) {
        String select = "SELECT * FROM " + Const.PRODUCTS_TABLE + " WHERE " + Const.PRODUCTS_ID + " = " + id;
        int count = 0;
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            ResultSet result = prSt.executeQuery();
            while (result.next()) {
                count++;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (count == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void Update_Product(int id, int Price, int Quantity, Date Date) {
        try {
            if (Price == 0) {
                String update = "UPDATE " + Const.PRODUCTS_TABLE + " SET " + Const.PRODUCTS_DATE + " = '" + Date + "', " +
                        Const.PRODUCTS_QUANTITY + "= " + Const.PRODUCTS_QUANTITY + " + " + Quantity +
                        " WHERE " + Const.PRODUCTS_ID + " = " + id;

                PreparedStatement prSt = getDbConnection().prepareStatement(update);
                prSt.executeUpdate();

            } else {
                String update = "UPDATE " + Const.PRODUCTS_TABLE + " SET " + Const.PRODUCTS_DATE + " = '" + Date + "', " +
                        Const.PRODUCTS_PRICE + " = " + Price + ", " + Const.PRODUCTS_QUANTITY + "= " +
                        Const.PRODUCTS_QUANTITY + " + " + Quantity + " WHERE " + Const.PRODUCTS_ID + " = " + id;

                PreparedStatement prSt = getDbConnection().prepareStatement(update);
                prSt.executeUpdate();

            }

            String select = "SELECT * FROM products WHERE idProducts =" + id;

            PreparedStatement prSt_select = getDbConnection().prepareStatement(select);
            ResultSet Rs = prSt_select.executeQuery();

            Update_Category(Rs);

            PreparedStatement prSt_select_second = getDbConnection().prepareStatement(select);
            ResultSet RS = prSt_select_second.executeQuery();

            Update_Manufacturer(RS);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void Update_Category(ResultSet Rs) {
        try {
            if (Rs.next()) {
                Rs.getString(1);
            }
            String Category = Rs.getString("Category");
            String update_categories = "UPDATE " + Const.CATEGORIES_TABLE + " SET " +
                    Const.CATEGORIES_NUMBER_OF_PRODUCTS_NAME + "= ?," +
                    Const.CATEGORIES_NUMBER_OF_GOODS + " = ?," +
                    Const.CATEGORIES_AVERAGE_PRICE + "= ?," +
                    Const.CATEGORIES_MAX_PRICE + "= ?," +
                    Const.CATEGORIES_MIN_PRICE + "= ?" + " WHERE " +
                    Const.CATEGORIES_NAME + "= ?";

            String sql_categories = "SELECT * FROM " + Const.PRODUCTS_TABLE +
                    " WHERE " + Const.PRODUCTS_CATEGORY + " = '" + Category + "'";

            Statement st_categories = getDbConnection().createStatement();
            ResultSet rs_categories = st_categories.executeQuery(sql_categories);

            int i = 1, number_of_goods = 0;
            double average = 0, max = Rs.getInt("Price"), min = Rs.getInt("Price");
            if (rs_categories.next()) {
                rs_categories.getString(1);
            }
            average += rs_categories.getDouble("Price");
            number_of_goods += rs_categories.getInt("Quantity");
            if (max < rs_categories.getDouble("Price"))
                max = rs_categories.getDouble("Price");
            if (min > rs_categories.getDouble("Price"))
                min = rs_categories.getDouble("Price");
            while (rs_categories.next()) {
                average += rs_categories.getDouble("Price");
                number_of_goods += rs_categories.getInt("Quantity");
                if (max < rs_categories.getDouble("Price"))
                    max = rs_categories.getDouble("Price");
                if (min > rs_categories.getDouble("Price"))
                    min = rs_categories.getDouble("Price");
                i++;
            }
            average /= i;
            double res = Math.round(average * 100) / 100;

            PreparedStatement prSt_upd_categories = getDbConnection().prepareStatement(update_categories);
            prSt_upd_categories.setInt(1, i);
            prSt_upd_categories.setInt(2, number_of_goods);
            prSt_upd_categories.setDouble(3, res);
            prSt_upd_categories.setDouble(4, max);
            prSt_upd_categories.setDouble(5, min);
            prSt_upd_categories.setString(6, Rs.getString("Category"));

            prSt_upd_categories.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void Update_Manufacturer(ResultSet Rs) {
        try {
            if (Rs.next()) {
                Rs.getString(1);
            }
            String select_manufacturers_products = "SELECT * FROM " + Const.PRODUCTS_TABLE +
                    " WHERE " + Const.PRODUCTS_MANUFACTURER + " LIKE '" + Rs.getString("Manufacturer") + "'";
            Statement st_manufacturers_products = getDbConnection().createStatement();
            ResultSet rs_manufacturers_products = st_manufacturers_products.executeQuery(select_manufacturers_products);
            double All_products = 0;
            while (rs_manufacturers_products.next()) {
                All_products += rs_manufacturers_products.getInt("Quantity");
            }

            String select_manufacturers_category = "SELECT " + Const.CATEGORIES_NAME +
                    " FROM " + Const.CATEGORIES_TABLE;
            Statement st_manufacturers_category = getDbConnection().createStatement();
            ResultSet rs_manufacturers_category = st_manufacturers_category.executeQuery(select_manufacturers_category);

            while (rs_manufacturers_category.next()) {
                String select_manufacturers_first = "SELECT * FROM " + Const.PRODUCTS_TABLE +
                        " WHERE " + Const.PRODUCTS_CATEGORY + "='" + rs_manufacturers_category.getString("CategoryName") + "'" +
                        " AND " + Const.PRODUCTS_MANUFACTURER + "='" + Rs.getString("Manufacturer") + "'";
                Statement st_upd_manufacturers_first = getDbConnection().createStatement();
                ResultSet re_upd_manufacturers_first = st_upd_manufacturers_first.executeQuery(select_manufacturers_first);
                int count_man = 0;
                double Nums_of_goods = 0, perc;
                while (re_upd_manufacturers_first.next())
                    count_man++;
                if (count_man == 0) {
                    continue;
                } else {
                    String select_manufacturers_second = "SELECT * FROM " + Const.PRODUCTS_TABLE +
                            " WHERE " + Const.PRODUCTS_CATEGORY + "='" + rs_manufacturers_category.getString("CategoryName") + "'" +
                            " AND " + Const.PRODUCTS_MANUFACTURER + "='" + Rs.getString("Manufacturer") + "'";
                    Statement st_upd_manufacturers = getDbConnection().createStatement();
                    ResultSet re_upd_manufacturers = st_upd_manufacturers.executeQuery(select_manufacturers_second);
                    while (re_upd_manufacturers.next()) {
                        Nums_of_goods += re_upd_manufacturers.getDouble("Quantity");
                    }
                    perc = Nums_of_goods / All_products * 100;
                    double percent = Math.ceil(perc * 100) / 100;

                    int nums = (int) Nums_of_goods;

                    String upd_manufacturers = "UPDATE " + Const.MANUFACTURERS_TABLE + " SET " +
                            Const.MANUFACTURERS_NUMBER_OF_GOODS + "=" + nums + "," +
                            Const.MANUFACTURERS_PERCENTAGE_OF_GOODS + "=" + percent + " WHERE " +
                            Const.MANUFACTURERS_NAME + "='" + Rs.getString("Manufacturer") + "'" +
                            " AND " + Const.MANUFACTURERS_CATEGORIES + "='" + rs_manufacturers_category.getString("CategoryName") + "'";
                    PreparedStatement prSt_manufacturers = getDbConnection().prepareStatement(upd_manufacturers);
                    prSt_manufacturers.executeUpdate();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void Add_Manufacturer(AddProduct addproduct) {
        try {
            String select_manufacturers = "SELECT * FROM products WHERE Manufacturer LIKE '" + addproduct.getManufacturer() + "'";
            PreparedStatement prSt_manufacturers = getDbConnection().prepareStatement(select_manufacturers);

            ResultSet rs_manufacturers = prSt_manufacturers.executeQuery();

            int All_products = 0;
            while (rs_manufacturers.next()) {
                All_products += rs_manufacturers.getInt("Quantity");
            }

            double percent;
            double Nums_of_goods = 0, perc;
            String select_manufacturers_ins = "SELECT * FROM " + Const.PRODUCTS_TABLE +
                    " WHERE " + Const.PRODUCTS_CATEGORY + " LIKE '" + addproduct.getCategory() + "'" +
                    " AND " + Const.PRODUCTS_MANUFACTURER + " LIKE '" + addproduct.getManufacturer() + "'";
            Statement st_upd = getDbConnection().createStatement();
            ResultSet re_upd = st_upd.executeQuery(select_manufacturers_ins);
            while (re_upd.next()) {
                Nums_of_goods += re_upd.getDouble("Quantity");
            }
            perc = Nums_of_goods / All_products * 100;
            percent = Math.ceil(perc * 100) / 100;

            String insert_manufacturers = "INSERT INTO " + Const.MANUFACTURERS_TABLE + " (" +
                    Const.MANUFACTURERS_NAME + "," + Const.MANUFACTURERS_CATEGORIES + "," +
                    Const.MANUFACTURERS_NUMBER_OF_GOODS + "," + Const.MANUFACTURERS_PERCENTAGE_OF_GOODS + ") " +
                    "VALUES(?,?,?,?)";
            PreparedStatement prSt_ins_manufacturers = getDbConnection().prepareStatement(insert_manufacturers);
            prSt_ins_manufacturers.setString(1, addproduct.getManufacturer());
            prSt_ins_manufacturers.setString(2, addproduct.getCategory());
            prSt_ins_manufacturers.setInt(3, addproduct.getQuantity());
            prSt_ins_manufacturers.setDouble(4, percent);

            prSt_ins_manufacturers.executeUpdate();

            String select_manufacturers_category = "SELECT " + Const.CATEGORIES_NAME +
                    " FROM " + Const.CATEGORIES_TABLE;
            Statement st_manufacturers_category = getDbConnection().createStatement();
            ResultSet rs_manufacturers_category = st_manufacturers_category.executeQuery(select_manufacturers_category);

            while (rs_manufacturers_category.next()) {
                String select_manufacturers_ins_first = "SELECT * FROM " + Const.PRODUCTS_TABLE +
                        " WHERE " + Const.PRODUCTS_CATEGORY + " LIKE '" + rs_manufacturers_category.getString("CategoryName") + "'" +
                        " AND " + Const.PRODUCTS_MANUFACTURER + " LIKE '" + addproduct.getManufacturer() + "'";
                Statement st_upd_manufacturers_first = getDbConnection().createStatement();
                ResultSet re_upd_manufacturers_first = st_upd_manufacturers_first.executeQuery(select_manufacturers_ins_first);
                int count_man = 0;
                double N_of_g = 0, percen;
                while (re_upd_manufacturers_first.next())
                    count_man++;
                if (count_man == 0) {
                    continue;
                } else if (count_man > 1) {
                    String select_manufacturers_ins_second = "SELECT * FROM " + Const.PRODUCTS_TABLE +
                            " WHERE " + Const.PRODUCTS_CATEGORY + " LIKE '" + rs_manufacturers_category.getString("CategoryName") + "'" +
                            " AND " + Const.PRODUCTS_MANUFACTURER + " LIKE '" + addproduct.getManufacturer() + "'";
                    Statement st_upd_manufacturers = getDbConnection().createStatement();
                    ResultSet re_upd_manufacturers = st_upd_manufacturers.executeQuery(select_manufacturers_ins_second);
                    while (re_upd_manufacturers.next()) {
                        N_of_g += re_upd_manufacturers.getDouble("Quantity");
                    }
                    percen = N_of_g / All_products * 100;
                    double percentage = Math.ceil(percen * 100) / 100;

                    int nums = (int) N_of_g;

                    String upd_manufacturers = "UPDATE " + Const.MANUFACTURERS_TABLE + " SET " +
                            Const.MANUFACTURERS_NUMBER_OF_GOODS + "=" + nums + "," +
                            Const.MANUFACTURERS_PERCENTAGE_OF_GOODS + "=" + percentage + " WHERE " +
                            Const.MANUFACTURERS_NAME + " LIKE '" + addproduct.getManufacturer() + "'" +
                            " AND " + Const.MANUFACTURERS_CATEGORIES + " LIKE '" + rs_manufacturers_category.getString("CategoryName") + "'";
                    PreparedStatement prSt_manufacturer = getDbConnection().prepareStatement(upd_manufacturers);
                    prSt_manufacturer.executeUpdate();
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ResultSet Pie() {
        String PieCharm = "Select * FROM " + Const.CATEGORIES_TABLE;
        Statement St;
        ResultSet result = null;
        try {
            St = getDbConnection().createStatement();
            result = St.executeQuery(PieCharm);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }


    public ObservableList<Product> SearchProduct(String name, String category, String manufacturer, String price_from, String price_up_to, String date_search) {
        ObservableList<Product> list = null;
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        try {
            Date date = null;
            String select = "SELECT * FROM " + Const.PRODUCTS_TABLE;
            if(date_search.length() != 0)
            {
                date = new java.sql.Date(format.parse(date_search).getTime());
            }

            String name_search, category_search, manufacturer_search, price_from_search, price_up_to_search, date_second_search;
            if(date_search.length() == 0)
            {
                date_second_search = "";
            } else if(name.length() == 0 && category.length() == 0 && manufacturer.length() == 0 && price_from.length() == 0 && price_up_to.length() == 0 )
            {
                date_second_search = Const.PRODUCTS_DATE + " = '" + date + "'";
            }
            else
            {
                date_second_search = " AND " + Const.PRODUCTS_DATE + " = '" + date + "'";
            }
            if(price_up_to.length() == 0)
            {
                price_up_to_search = "";
            } else if(name.length() == 0 && category.length() == 0 && manufacturer.length() == 0 && price_from.length() == 0)
            {
                price_up_to_search = Const.PRODUCTS_PRICE + " <= " + price_up_to;
            }
            else
            {
                price_up_to_search = " AND " + Const.PRODUCTS_PRICE + " <= " + price_up_to;
            }
            if(price_from.length() == 0)
            {
                price_from_search = "";
            } else if (name.length() == 0 && category.length() == 0 && manufacturer.length() == 0)
            {
                price_from_search = Const.PRODUCTS_PRICE + " >= " + price_from;
            } else {
                price_from_search = " AND " + Const.PRODUCTS_PRICE + " >= " + price_from;
            }
            if(manufacturer.length() == 0)
            {
                manufacturer_search = "";
            } else if(name.length() == 0 && category.length() == 0)
            {
                manufacturer_search = Const.PRODUCTS_MANUFACTURER + " LIKE '" + manufacturer + "'";
            }
            else
            {
                manufacturer_search = " AND " + Const.PRODUCTS_MANUFACTURER + " LIKE '" + manufacturer + "'";
            }
            if(category.length() == 0)
            {
                category_search = "";
            } else if(name.length() == 0)
            {
                category_search = Const.PRODUCTS_CATEGORY + " LIKE '" + category + "'";
            }
            else
            {
                category_search = " AND " + Const.PRODUCTS_CATEGORY + " LIKE '" + category + "'";
            }
            if (name.length() == 0)
            {
                name_search = "";
            }
            else
            {
                name_search = Const.PRODUCTS_NAME + " LIKE '" + name + "'";
            }
            if(name.length() != 0 || category.length() != 0 || manufacturer.length() != 0 || price_from.length() != 0 || price_up_to.length() != 0 || date_search.length() != 0)
            {
                select = "SELECT * FROM " + Const.PRODUCTS_TABLE + " WHERE " + name_search + category_search + manufacturer_search + price_from_search + price_up_to_search + date_second_search;
            }
            list = FXCollections.observableArrayList();
            Statement St;
            St = getDbConnection().createStatement();
            ResultSet rs = St.executeQuery(select);
            while (rs.next()) {
                list.add(new Product(Integer.parseInt(rs.getString("idProducts")), rs.getString("Name"), rs.getString("Category"), rs.getString("Manufacturer"), parseDouble(rs.getString("Price")), Integer.parseInt(rs.getString("Quantity")), rs.getString("Unit"), rs.getDate("Date")));
            }
        }catch (SQLException | ClassNotFoundException | ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int All_manufacturers_products(String manufacturer)
    {
        String select_manufacturers_products = "SELECT * FROM " + Const.PRODUCTS_TABLE +
                " WHERE " + Const.PRODUCTS_MANUFACTURER + " LIKE '" + manufacturer + "'";
        int All_products = 0;
        try {
            Statement st_manufacturers_products = getDbConnection().createStatement();
            ResultSet rs_manufacturers_products = st_manufacturers_products.executeQuery(select_manufacturers_products);
            while (rs_manufacturers_products.next()) {
                All_products += rs_manufacturers_products.getInt("Quantity");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return All_products;
    }
}

