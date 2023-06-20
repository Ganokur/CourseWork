package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;


public class Controller {

    //authorization
    @FXML
    private Button SignInAsAdministrator;
    @FXML
    private TextField Login_Field;
    @FXML
    private PasswordField Password_Field;
    @FXML
    private Text Username;
    @FXML
    private Button ExitAsAdministrator;
    @FXML
    private Text InitializeError;
    @FXML
    private Tab admintab;

    //Products Table
    @FXML
    private TableView<Product> Products;
    @FXML
    private TableColumn<Product, Integer> Products_Id;
    @FXML
    private TableColumn<Product, String> Products_Name;
    @FXML
    private TableColumn<Product, String> Products_Category;
    @FXML
    private TableColumn<Product, String> Products_Manufacturer;
    @FXML
    private TableColumn<Product, Integer> Products_Price;
    @FXML
    private TableColumn<Product, Integer> Products_Quantity;
    @FXML
    private TableColumn<Product, String> Products_Unit;
    @FXML
    private TableColumn<Product, Date> Products_Date;

    //Delete product
    @FXML
    private TextField Delete_Product;
    @FXML
    private Button Delete_Button;
    @FXML
    private Text Delete_message;

    //Add product
    @FXML
    private TextField Add_Product_Name;
    @FXML
    private TextField Add_Product_Category;
    @FXML
    private TextField Add_Product_Manufacturer;
    @FXML
    private TextField Add_Product_Price;
    @FXML
    private TextField Add_Product_Quantity;
    @FXML
    private TextField Add_Product_Unit;
    @FXML
    private DatePicker Add_Product_Date;
    @FXML
    private Button Add_Button;
    @FXML
    private Text Add_message;

    //Update product
    @FXML
    private TextField Update_Product_Minus_Quantity;
    @FXML
    private DatePicker Update_Product_Date;
    @FXML
    private TextField Update_Product_Plus_Quantity;
    @FXML
    private Button Update_button;
    @FXML
    private TextField Update_product_price;
    @FXML
    private TextField Update_product_id;
    @FXML
    private Text Update_Message;


    //Categories table
    @FXML
    private TableView<Categories> Categories;
    @FXML
    private TableColumn<Categories, String> Categories_name;
    @FXML
    private TableColumn<Categories, Integer> Categories_Number_Of_Products_Names;
    @FXML
    private TableColumn<Categories, Integer> Categories_Number_Of_Goods;
    @FXML
    private TableColumn<Categories, Double> Categories_Average_Price;
    @FXML
    private TableColumn<Categories, Double> Categories_Max_Price;
    @FXML
    private TableColumn<Categories, Double> Categories_Min_Price;

    //Manufacturers table
    @FXML
    private TableView<Manufacturers> Manufacturers;
    @FXML
    private TableColumn<Manufacturers, String> Manufacturers_Name;
    @FXML
    private TableColumn<Manufacturers, String> Manufacturers_Category;
    @FXML
    private TableColumn<Manufacturers, Integer> Manufacturers_Number_Of_Goods;
    @FXML
    private TableColumn<Manufacturers, Double> Manufacturers_Percent_Of_Goods;
    @FXML
    private TextField Manufacturers_search;
    @FXML
    private Text Manufacturers_Message;
    @FXML
    private Text Manufacturer_All_Products;

    //Search
    @FXML
    private TextField Search_Name;
    @FXML
    private TextField Search_Category;
    @FXML
    private TextField Search_Manufacturer;
    @FXML
    private TextField Search_price_from;
    @FXML
    private TextField Search_price_up_to;
    @FXML
    private DatePicker Search_Date;
    @FXML
    private Button Search_button;
    @FXML
    private Text Search_Message;


    //pie chart
    @FXML
    private PieChart pieChart;


    @FXML
    void UpdateProduct(ActionEvent event)
    {
        if(Update_product_id.getText().trim().length() == 0 || Update_Product_Date.getEditor().getText().trim().length() == 0)
        {
            Update_Message.setText("Заполните поля id и Дата!");
        }
        else {
            DBHandler dbHandler = new DBHandler();
            int Update_id = Integer.parseInt(Update_product_id.getText());

            if (!dbHandler.select_id(Update_id)) {
                Update_Message.setText("Такого id нет!");
            }
            else
            {
                int new_price = 0;
                if(Update_Product_Minus_Quantity.getText().trim().length() == 0)
                {
                    Update_Product_Minus_Quantity.setText("0");
                }
                if(Update_Product_Plus_Quantity.getText().trim().length() == 0)
                {
                    Update_Product_Plus_Quantity.setText("0");
                }
                if(Update_product_price.getText().trim().length() != 0)
                {
                    new_price = Integer.parseInt(Update_product_price.getText());
                }

                int minus_quantity = Integer.parseInt(Update_Product_Minus_Quantity.getText());
                int plus_quantity = Integer.parseInt(Update_Product_Plus_Quantity.getText());


                String date_string = Update_Product_Date.getEditor().getText();
                DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                try {
                    Date date = new java.sql.Date(format.parse(date_string).getTime());

                    dbHandler.Update_Product(Update_id, new_price, plus_quantity - minus_quantity, date);
                    initialize();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Update_Message.setText("");
                Update_product_id.setText("");
                Update_Product_Minus_Quantity.setText("");
                Update_Product_Plus_Quantity.setText("");
                Update_product_price.setText("");
                Update_Product_Date.getEditor().setText("");
                initialize();
            }
        }
    }

    @FXML
    void AddProduct(ActionEvent event)
    {
        if(Add_Product_Name.getText().trim().length() == 0 || Add_Product_Category.getText().trim().length() == 0 ||
                Add_Product_Manufacturer.getText().trim().length() == 0 || Add_Product_Price.getText().trim().length() == 0 ||
                Add_Product_Quantity.getText().trim().length() == 0 || Add_Product_Unit.getText().trim().length() == 0 ||
                Add_Product_Date.getEditor().getText().trim().length() == 0)
        {
            Add_message.setText("Заполните все поля!");
        }
        else
        {
            DBHandler dbHandler = new DBHandler();

            String Name = Add_Product_Name.getText();
            String Category = Add_Product_Category.getText();
            String Manufacturer = Add_Product_Manufacturer.getText();
            int Price = Integer.parseInt(Add_Product_Price.getText());
            int Quantity = Integer.parseInt(Add_Product_Quantity.getText());
            String Unit = Add_Product_Unit.getText();
            String date_string = Add_Product_Date.getEditor().getText();
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            try {
                Date date = new java.sql.Date(format.parse(date_string).getTime());

                AddProduct addProduct = new AddProduct(Name, Category, Manufacturer, Price, Quantity, Unit, (java.sql.Date) date);
                dbHandler.Add_Products(addProduct);
                initialize();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            Add_message.setText("");
            Add_Product_Name.setText("");
            Add_Product_Category.setText("");
            Add_Product_Manufacturer.setText("");
            Add_Product_Price.setText("");
            Add_Product_Quantity.setText("");
            Add_Product_Unit.setText("");
            Add_Product_Date.getEditor().setText("");

            initialize();
        }

    }

    @FXML
    void DeleteProduct(ActionEvent event)
    {
        String text_id = Delete_Product.getText().trim();
        if(text_id.trim().length() == 0)
            Delete_message.setText("Введите id!");
        else
        {
            DBHandler dbHandler = new DBHandler();
            int id = Integer.parseInt(Delete_Product.getText());
            if (!dbHandler.select_id(id))
            {
                Delete_message.setText("Такого id нет!");
                Delete_Product.setText("");
            }
            else
            {
                dbHandler.Delete_Products(id);
                Delete_Product.setText("");
                Delete_message.setText("");
                initialize();
            }
        }
    }

    @FXML
    private void initialize() {
        DBHandler dbHandler = new DBHandler();
        ObservableList<Product> product_data = dbHandler.getDataProducts();

        Products_Id.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
        Products_Name.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        Products_Category.setCellValueFactory(new PropertyValueFactory<Product, String>("category"));
        Products_Manufacturer.setCellValueFactory(new PropertyValueFactory<Product, String>("manufacturer"));
        Products_Price.setCellValueFactory(new PropertyValueFactory<Product, Integer>("price"));
        Products_Quantity.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));
        Products_Unit.setCellValueFactory(new PropertyValueFactory<Product, String>("unit"));
        Products_Date.setCellValueFactory(new PropertyValueFactory<Product, Date>("date"));

        Products.setItems(product_data);


        ObservableList<Categories> Categories_Data = dbHandler.getDataCategories();

        Categories_name.setCellValueFactory(new PropertyValueFactory<Categories, String>("CategoryName"));
        Categories_Number_Of_Products_Names.setCellValueFactory(new PropertyValueFactory<Categories, Integer>("NumberOfProductsNames"));
        Categories_Number_Of_Goods.setCellValueFactory(new PropertyValueFactory<Categories, Integer>("NumberOfGoods"));
        Categories_Average_Price.setCellValueFactory(new PropertyValueFactory<Categories, Double>("AveragePrice"));
        Categories_Max_Price.setCellValueFactory(new PropertyValueFactory<Categories, Double>("MaxPrice"));
        Categories_Min_Price.setCellValueFactory(new PropertyValueFactory<Categories, Double>("MinPrice"));

        Categories.setItems(Categories_Data);


        ResultSet result = dbHandler.Pie();
        ObservableList<PieChart.Data> data = null;
        try {
            if (result.next())
            {
                result.getString(1);
                data = FXCollections.observableArrayList(
                        new PieChart.Data(result.getString("CategoryName"), Integer.parseInt(result.getString("NumberOfGoods"))));
            }
            while (result.next())
            {
                data.add(new PieChart.Data(result.getString("CategoryName"), Integer.parseInt(result.getString("NumberOfGoods"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PieChart chart = new PieChart(data);
        chart.setTitle("Categories");
        pieChart.setData(data);
    }

        @FXML
    void SignIn(ActionEvent event)
    {

        String UserLogin = Login_Field.getText().trim();
        String UserPassword = Password_Field.getText().trim();

        if(!UserLogin.equals("") && !UserPassword.equals(""))
        {
            loginUser(UserLogin, UserPassword);
        }
        else
        {
            InitializeError.setVisible(true);
        }
    }

    private void loginUser(String UserLogin, String UserPassword)
    {
        DBHandler dbHandler = new DBHandler();
        ResultSet result = dbHandler.getUser(UserLogin, UserPassword);


        String username = null;

        int counter = 0;

        try
        {
            while(result.next())
            {
                counter++;
                username = result.getString("Username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(counter >= 1)
        {
            Username.setText(username);

            admintab.setDisable(false);
            SignInAsAdministrator.setVisible(false);
            Login_Field.setVisible(false);
            Password_Field.setVisible(false);
            Username.setVisible(true);
            ExitAsAdministrator.setVisible(true);
            InitializeError.setVisible(false);
        }
        else
        {
            InitializeError.setVisible(true);
        }
    }

    @FXML
    void ExitFromSystem(ActionEvent event)
    {
        Login_Field.setText("");
        Password_Field.setText("");
        admintab.setDisable(true);
        SignInAsAdministrator.setVisible(true);
        Login_Field.setVisible(true);
        Password_Field.setVisible(true);
        Username.setVisible(false);
        ExitAsAdministrator.setVisible(false);
    }

    @FXML
    void Manufacturers_show(ActionEvent event)
    {

        if (Manufacturers_search.getText().trim().length() == 0) {
            Manufacturers_Message.setText("Заполните поле!");
        }
        else
        {
            String search = Manufacturers_search.getText();
            DBHandler dbHandler = new DBHandler();
            ObservableList<Manufacturers> Manufacturers_Data = dbHandler.getDataManufacturers(search);

            Manufacturers_Name.setCellValueFactory(new PropertyValueFactory<Manufacturers, String>("ManufacturerName"));
            Manufacturers_Category.setCellValueFactory(new PropertyValueFactory<Manufacturers, String>("Categories"));
            Manufacturers_Number_Of_Goods.setCellValueFactory(new PropertyValueFactory<Manufacturers, Integer>("NumberOfGoods"));
            Manufacturers_Percent_Of_Goods.setCellValueFactory(new PropertyValueFactory<Manufacturers, Double>("PercentageOfGoods"));

            Manufacturers.setItems(Manufacturers_Data);

            int products = dbHandler.All_manufacturers_products(Manufacturers_search.getText());

            Manufacturer_All_Products.setText("Общее кол-во товара: " + products);
            Manufacturers_Message.setText("");
            Manufacturers_search.setText("");
        }
    }


    @FXML
    void Search(ActionEvent event) {
        DBHandler dbHandler = new DBHandler();

        String Name = Search_Name.getText();
        String Category = Search_Category.getText();
        String Manufacturer = Search_Manufacturer.getText();
        String Price_from = Search_price_from.getText();
        String Price_up_to = Search_price_up_to.getText();
        String date_search = Search_Date.getEditor().getText();


        ObservableList<Product> product_data = dbHandler.SearchProduct(Name, Category, Manufacturer, Price_from, Price_up_to, date_search);

        Products_Id.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
        Products_Name.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        Products_Category.setCellValueFactory(new PropertyValueFactory<Product, String>("category"));
        Products_Manufacturer.setCellValueFactory(new PropertyValueFactory<Product, String>("manufacturer"));
        Products_Price.setCellValueFactory(new PropertyValueFactory<Product, Integer>("price"));
        Products_Quantity.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));
        Products_Unit.setCellValueFactory(new PropertyValueFactory<Product, String>("unit"));
        Products_Date.setCellValueFactory(new PropertyValueFactory<Product, Date>("date"));

        Products.setItems(product_data);


        Search_Message.setText("");
    }


    @FXML
    public void Delete_Enter_Id(javafx.scene.input.KeyEvent keyEvent) {
        Pattern p = Pattern.compile("(\\d+\\.?\\d*)?");
        Delete_Product.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!p.matcher(newValue).matches()) Delete_Product.setText(oldValue);
        });
    }
    @FXML
    public void Update_Enter_Id(javafx.scene.input.KeyEvent keyEvent) {
        Pattern p = Pattern.compile("(\\d+\\.?\\d*)?");
        Update_product_id.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!p.matcher(newValue).matches()) Update_product_id.setText(oldValue);
        });
    }
    @FXML
    public void Update_Enter_Minus_Quantity(javafx.scene.input.KeyEvent keyEvent) {
        Pattern p = Pattern.compile("(\\d+\\.?\\d*)?");
        Update_Product_Minus_Quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!p.matcher(newValue).matches()) Update_Product_Minus_Quantity.setText(oldValue);
        });
    }
    @FXML
    public void Update_Enter_Plus_Quantity(javafx.scene.input.KeyEvent keyEvent) {
        Pattern p = Pattern.compile("(\\d+\\.?\\d*)?");
        Update_Product_Plus_Quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!p.matcher(newValue).matches()) Update_Product_Plus_Quantity.setText(oldValue);
        });
    }
    @FXML
    public void Update_Enter_Price(javafx.scene.input.KeyEvent keyEvent) {
        Pattern p = Pattern.compile("(\\d+\\.?\\d*)?");
        Update_product_price.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!p.matcher(newValue).matches()) Update_product_price.setText(oldValue);
        });
    }
    @FXML
    public void Add_Enter_Price(javafx.scene.input.KeyEvent keyEvent) {
        Pattern p = Pattern.compile("(\\d+\\.?\\d*)?");
        Add_Product_Price.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!p.matcher(newValue).matches()) Add_Product_Price.setText(oldValue);
        });
    }
    @FXML
    public void Add_Enter_Quantity(javafx.scene.input.KeyEvent keyEvent) {
        Pattern p = Pattern.compile("(\\d+\\.?\\d*)?");
        Add_Product_Quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!p.matcher(newValue).matches()) Add_Product_Quantity.setText(oldValue);
        });
    }
}



