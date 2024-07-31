package com.zinger.demo;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DemoApplicationTests {

	@Nested
	@SpringBootTest
	@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@Order(1)
	class TestWithSpecificProperties {

		@Autowired
		private JdbcTemplate jdbcTemplate;

		@Test
		@Order(1)
		void testWithSpecificProperties() {
			// Create the table and insert a record into the database
			jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY, myvalue VARCHAR(255));");
			jdbcTemplate.update("INSERT INTO test_table (id, myvalue) VALUES (1, 'test-value')");
			System.out.println("Record inserted");
		}
	}

	@Nested
	@SpringBootTest
	@TestPropertySource(properties = {"some-property=other-value"})
	@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@Order(2)
	class TestWithSpecificProperties2 {

		@Autowired
		private JdbcTemplate jdbcTemplate;

		@Test
		@Order(1)
		void testWithSpecificProperties2() {
			// Verify the record exists and has the correct value
			String value = jdbcTemplate.queryForObject(
					"SELECT myvalue FROM test_table WHERE id = 1",
					String.class
			);
			assertEquals("test-value", value);
			System.out.println("Record verified");

			// Modify the record value
			jdbcTemplate.update("UPDATE test_table SET myvalue = 'modified-value' WHERE id = 1");
			System.out.println("Record modified");
		}
	}

	@Nested
	@SpringBootTest
	@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@Order(3)
	class TestWithSpecificProperties3 {

		@Autowired
		private JdbcTemplate jdbcTemplate;

		@Test
		@Order(1)
		void testWithSpecificProperties3() {
			// Validate the modified value
			String value = jdbcTemplate.queryForObject(
					"SELECT myvalue FROM test_table WHERE id = 1",
					String.class
			);
			assertEquals("modified-value", value);
			System.out.println("Modified value validated");
		}
	}

	@Nested
	@SpringBootTest
	@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@Order(0) // Ensure this runs before other tests
	class SanityCheck {

		@Autowired
		private JdbcTemplate jdbcTemplate;

		@Test
		@Order(1)
		void testDatabaseConnection() {
			try {
				// Simple query to check database connection
				String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
				assertEquals("1", result);
				System.out.println("Database connection sanity check passed");
			} catch (Exception e) {
				e.printStackTrace();
				fail("Database connection sanity check failed: " + e.getMessage());
			}
		}
	}
}
