package dk.cit.fyp.service;

public interface ImageService {
	
	byte[] getBytes(String filePath);
	
	String getImageSource(byte[] bytes);
	
	void storeLastImgPath(String filePath);
	
	String getLastImagePath();

}
