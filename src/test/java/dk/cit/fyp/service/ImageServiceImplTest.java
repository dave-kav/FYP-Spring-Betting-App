package dk.cit.fyp.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class ImageServiceImplTest {

	ImageService imageService = new ImageServiceImpl();
	
	@Test
	public void testGetImageSource() {
		String testString = "this is a test string";
		byte[] bytesArr = testString.getBytes();
		
		String dataUrl = imageService.getImageSource(bytesArr); 
		
		assertNotNull(dataUrl);
		assertTrue(dataUrl.contains("data:image/jpeg;base64"));
	}
}
