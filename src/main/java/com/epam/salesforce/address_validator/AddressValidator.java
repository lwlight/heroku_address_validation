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
    }*/
    @RequestMapping(value = "/{address}", method = RequestMethod.GET)
    ResponseEntity<String> address(@PathVariable String address){
        System.out.println("DB URL :: " + dbUrl);
        try(Connection connection = dataSource.getConnection();){
            Statement statement = connection.createStatement();
//            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Rejected_Address (address varchar(50))");

            ResultSet resultSet = statement.executeQuery("SELECT address FROM Rejected_Address WHERE address=" + address);
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


    @Bean
    public DataSource dataSource() throws SQLException {
        System.out.println("DB URL in dataSource :: " + dbUrl);
        if (dbUrl == null || dbUrl.isEmpty()) {
            return new HikariDataSource();
        } else {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            return new HikariDataSource(config);
        }
    }
}
