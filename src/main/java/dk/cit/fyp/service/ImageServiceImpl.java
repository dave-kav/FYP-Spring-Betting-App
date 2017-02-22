package dk.cit.fyp.service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {

	/**
	 * Takes in a filePath and returns the file as bytes 
	 */
	@Override
	public byte[] getBytes(String filePath) {
		// convert image to bytes
        FileInputStream fis = null; 
        ByteArrayOutputStream bos = null;
        byte[] fileBytes = null;
        
        try {
	        fis = new FileInputStream(filePath);
	        bos = new ByteArrayOutputStream();
	        int b;
	        byte[] buffer = new byte[1024];
	        
	        while((b=fis.read(buffer))!=-1){
	           bos.write(buffer,0,b);
	        }
	        
	        fileBytes = bos.toByteArray();
	        
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
        	try {
	        	fis.close();
	        	bos.close();
	        	
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        }
		return fileBytes;
	}
}
