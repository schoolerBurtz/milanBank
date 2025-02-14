package me.fingolfin;

import java.sql.*;
import java.text.SimpleDateFormat;

public class Bank {
    private static Bank bank = new Bank();
    private Data data;
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Bank() {
        data = new Data();
    }

    public static Bank getInstance() {
        return bank;
    }

    public void getCustomers() throws SQLException{ //added bc its whining about the "unhandled exception i handled somehwere else bruh
        ResultSet set = data.getResults("select name, surname, id from customers;");
        if (set == null) return;
        while (set.next()) {
            String id = set.getString("id");
            String name =set.getString("name");
            String surname = set.getString("surname");
            System.out.println(id +  " | " + name + " | " + surname);
        }
    }

    public void addCustomer(String name, String surname, String worth, String plz, String addr) {
        String[] rows = {"name", "surname", "worth", "PLZ", "addr", "created_at"};

        Timestamp stamp = new Timestamp(System.currentTimeMillis());

        String[] values = {name, surname, worth, plz, addr, sdf3.format(stamp)};
        data.insert("customers", rows, values);
    }

    public void getCustomerInfoByName(String name) throws SQLException{
        ResultSet set = data.getResults("select * from customers where name=\"" +name+"\";");
        if (set == null) {
            System.out.println(name + " not found!");
            return;
        }
        System.out.println("Displaying results for " +name);
        System.out.println("Full name: " + set.getString("surname") + " " + set.getString("name"));
        System.out.println("ID: " + set.getString("id"));
        System.out.println("Net Worth: " + set.getString("worth"));
        System.out.println("PLZ: " + set.getString("PLZ"));
        System.out.println("Address: " + set.getString("addr"));
        System.out.println("Account Created at: " + set.getString("created_at"));
    }

    public void getCustomerInfoByID(String id) throws SQLException{
        ResultSet set = data.getResults("select * from customers where id=\"" +id+"\";");
        if (set == null) {
            System.out.println(id + " not found!");
            return;
        }
        System.out.println("Displaying results for ID: " +id);
        System.out.println("Full name: " + set.getString("surname") + " " + set.getString("name"));
        System.out.println("ID: " + set.getString("id"));
        System.out.println("Net Worth: " + set.getString("worth"));
        System.out.println("PLZ: " + set.getString("PLZ"));
        System.out.println("Address: " + set.getString("addr"));
        System.out.println("Account Created at: " + set.getString("created_at"));
    }

    @Deprecated //moved to Data.java
    public void setDatabankUp() {
            String create_customers = "create table if not exists customers(id integer PRIMARY KEY, name text, "
                    + "surname text, worth real, PLZ text, addr text, created_at text);";
            System.out.println("database up and running");
            data.run(create_customers);
    }

    public void updateCustomer(String id, String row, String value) throws SQLException {
        ResultSet set = data.getResults(String.format("select %s from customers where id = \"%s\";", row, id));
        assert set != null;
        String val_old = set.getString(row);
        data.updateVal("customers", row, value, "id", id);
        System.out.printf("changed %s to %s%n", val_old, value);
    }
    
    //TODO: working on worth, should change amount to double later
    public void transfer(String senderID, String recieverID, double amount) {
        if (amount < 0) {
            System.out.println("negative ammounts not supported");
            return;
        }
        try {
        ResultSet set = data.getResults("select worth from customers where id  = \"" + senderID + "\"");
        double amount_old = set.getDouble("worth");
        double new_amount = amount_old - amount;
        updateCustomer(senderID, "worth", String.valueOf(new_amount));
        
        System.out.println("sender updated");
        
        set = data.getResults("select worth from customers where id  = \"" + recieverID + "\"");
        amount_old = set.getInt("worth");
        new_amount = amount_old + amount;
        updateCustomer(recieverID, "worth", String.valueOf(new_amount));
        
        System.out.println("receiver updated");
            //TODO: add accurate Exception for parsing String to Integer and SQLException
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getSpecificValueByID(String val, String id) {
        System.out.println(" the usable formats are name, surname, amount in bank, plz, addr and date created");
        String query = String.format("select %s from customers where id=\"%s\"", val, id);
        ResultSet set = data.getResults(query);
        try {
            System.out.printf(" Value for %s of customer with ID %s = %s%n", val, id, set.getString(val));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void getIDbyName(String name) {
        String query = String.format("select id from customers where name=\"%s\"", name);
        ResultSet set = data.getResults(query);
        try {
            System.out.printf(" the id for %s is %s", name, set.getString("id"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throwables.getErrorCode();
            throwables.getMessage( );
        }
    }
}
