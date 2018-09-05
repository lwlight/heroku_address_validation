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
import javax.websocket.server.PathParam;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@RestController
@RequestMapping("/")
@SpringBootApplication
public class AddressValidator {

    private static final String INSERT_ADDRESS = "INSERT INTO Rejected_Address (address) VALUES ('reject')";

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(AddressValidator.class, args);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    ResponseEntity<String> intro(){
        return new ResponseEntity<String>("Hello there", HttpStatus.OK);
    }

    @RequestMapping(value = "/{address}", method = RequestMethod.GET)
    ResponseEntity<String> address(@PathVariable String address){
/*        if ("reject".equals(address)){
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<String>(HttpStatus.OK);
        }*/
        System.out.println(INSERT_ADDRESS);
        try (Connection connection = dataSource.getConnection()) {

            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Rejected_Address (address varchar(50))");
            System.out.println("LINE 58: BEFORE ERROR");
//            statement.executeUpdate(INSERT_ADDRESS);
//            statement.executeUpdate("INSERT INTO Rejected_Address (address) VALUES (reject)");
            ResultSet rs = statement.executeQuery("SELECT address FROM Rejected_Address");
//
//            if (rs.next()){
//                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//            } else {
//                return new ResponseEntity<>(HttpStatus.OK);
//            }
            while (rs.next()){
                System.out.println(rs.getString("address"));
            }




        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public DataSource dataSource() throws SQLException {
        if (dbUrl == null || dbUrl.isEmpty()) {
            return new HikariDataSource();
        } else {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            return new HikariDataSource(config);
        }
    }
}
