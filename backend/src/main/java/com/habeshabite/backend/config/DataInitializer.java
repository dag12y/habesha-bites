package com.habeshabite.backend.config;

import com.habeshabite.backend.entity.Driver;
import com.habeshabite.backend.entity.Table;
import com.habeshabite.backend.repository.DriverRepository;
import com.habeshabite.backend.repository.TableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(TableRepository tableRepository) {
        return args -> {
            // Check if tables already exist
            if (tableRepository.count() > 0) {
                logger.info("Tables already exist in database. Skipping initialization.");
                return;
            }

            logger.info("Initializing restaurant tables...");

            // Create tables with different capacities
            Table table1 = new Table();
            table1.setTableNumber(1);
            table1.setCapacity(2);
            table1.setIsActive(true);
            tableRepository.save(table1);

            Table table2 = new Table();
            table2.setTableNumber(2);
            table2.setCapacity(2);
            table2.setIsActive(true);
            tableRepository.save(table2);

            Table table3 = new Table();
            table3.setTableNumber(3);
            table3.setCapacity(4);
            table3.setIsActive(true);
            tableRepository.save(table3);

            Table table4 = new Table();
            table4.setTableNumber(4);
            table4.setCapacity(4);
            table4.setIsActive(true);
            tableRepository.save(table4);

            Table table5 = new Table();
            table5.setTableNumber(5);
            table5.setCapacity(4);
            table5.setIsActive(true);
            tableRepository.save(table5);

            Table table6 = new Table();
            table6.setTableNumber(6);
            table6.setCapacity(6);
            table6.setIsActive(true);
            tableRepository.save(table6);

            Table table7 = new Table();
            table7.setTableNumber(7);
            table7.setCapacity(6);
            table7.setIsActive(true);
            tableRepository.save(table7);

            Table table8 = new Table();
            table8.setTableNumber(8);
            table8.setCapacity(8);
            table8.setIsActive(true);
            tableRepository.save(table8);

            Table table9 = new Table();
            table9.setTableNumber(9);
            table9.setCapacity(8);
            table9.setIsActive(true);
            tableRepository.save(table9);

            Table table10 = new Table();
            table10.setTableNumber(10);
            table10.setCapacity(10);
            table10.setIsActive(true);
            tableRepository.save(table10);

            logger.info("Successfully initialized {} restaurant tables", tableRepository.count());
        };
    }

    @Bean
    CommandLineRunner initDrivers(DriverRepository driverRepository) {
        return args -> {
            if (driverRepository.count() > 0) {
                logger.info("Drivers already exist. Skipping driver initialization.");
                return;
            }
            logger.info("Initializing drivers...");
            Driver d1 = new Driver();
            d1.setFullName("Abebe Kebede");
            d1.setEmail("abebe.driver@habeshabites.com");
            d1.setPhone("+251911000001");
            d1.setStatus(Driver.DriverStatus.AVAILABLE);
            d1.setIsActive(true);
            driverRepository.save(d1);

            Driver d2 = new Driver();
            d2.setFullName("Tigist Hailu");
            d2.setEmail("tigist.driver@habeshabites.com");
            d2.setPhone("+251922000002");
            d2.setStatus(Driver.DriverStatus.AVAILABLE);
            d2.setIsActive(true);
            driverRepository.save(d2);

            logger.info("Successfully initialized {} drivers", driverRepository.count());
        };
    }
}
