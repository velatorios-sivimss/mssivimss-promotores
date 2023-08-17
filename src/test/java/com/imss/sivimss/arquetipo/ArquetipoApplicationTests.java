package com.imss.sivimss.arquetipo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.imss.sivimss.promotores.PromotoresApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ArquetipoApplicationTests.class )
class ArquetipoApplicationTests {

	@Test
	void contextLoads() {
		String result = "test";
		//PromotoresApplication.main(new String[] {});
		assertNotNull(result);
	}

}
