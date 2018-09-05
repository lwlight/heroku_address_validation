package com.epam.salesforce.address_validator;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/")
@SpringBootApplication
public class AddressValidator {

    private static final String TABLE_NAME = "Rejected_Address";

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Autowired
    private DataSource dataSource;

    private static final Set<String> restrictedAddresses = new HashSet<>(3);
    static{
        restrictedAddresses.add("reject");
        restrictedAddresses.add("badAddress");
        restrictedAddresses.add("stop");
    }

    public static void main(String[] args) {
        SpringApplication.run(AddressValidator.class, args);
    }

/*    @RequestMapping(value = "/{address}", method = RequestMethod.GET)
    ResponseEntity<String> address(@PathVariable String address){
        if(restrictedAddresses.contains(address)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
    */

    @RequestMapping(value = "/{address}", method = RequestMethod.GET)
    ResponseEntity<String> isRejected(@PathVariable String address){
        System.out.println("DB URL :: " + dbUrl);
        try(Connection connection = dataSource.getConnection()){
            Statement statement = connection.createStatement();
//            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Rejected_Address (address varchar(50))");

            ResultSet resultSet = statement.executeQuery("SELECT address FROM " + TABLE_NAME + " WHERE address='" + address + "'");
            if (resultSet.next()){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else {
                return new ResponseEntity<>(HttpStatus.OK);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{address}", method = RequestMethod.POST)
    ResponseEntity<String> addRejected(@PathVariable String address){
        try(Connection connection = dataSource.getConnection()){
            Statement statement = connection.createStatement();
            int inserted = statement.executeUpdate("INSERT INTO " + TABLE_NAME + " VALUES ('" + address + "')");
            if (inserted != 0){
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
    }


    @Bean
    public DataSource dataSource() throws SQLException {
        System.out.println("DB URL in dataSource :: " + dbUrl);
        if (dbUrl == null || dbUrl.isEmpty()) {
            return new HikariDataSource();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        HikariDataSource hikariDataSource = new HikariDataSource(config);
        try(Connection connection = hikariDataSource.getConnection()){
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (address varchar(50))");
        }
        return hikariDataSource;
    }
}
