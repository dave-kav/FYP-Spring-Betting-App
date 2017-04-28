package dk.cit.fyp.config;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class WebConfigTest extends WebConfig {

	WebConfig webConfig = new WebConfig();
	
	@Test
	public void testReleaseImageInterceptor() {
		assertNotNull(webConfig.releaseImageInterceptor());
	}
}
