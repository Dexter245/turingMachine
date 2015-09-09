package turingMachine;

import java.io.IOException;
import java.io.InputStream;

public class Utilities{

	/** Reads the content of a given file as a String and returns it.
	 * @return the content of the given file as a String */
	public static String getFileContentAsString(InputStream inputStream) throws IOException{
		String data = "";
		byte[] buffer = new byte[1024];
		int numRead = -1;
		
		while(true){
			numRead = inputStream.read(buffer);
			if(numRead == 1024){
				data += new String(buffer);
			}
			else{
				if(numRead != -1)
					data += new String(buffer, 0, numRead);
				break;
			}
		}
		
		return data;
		
	}

	
}
