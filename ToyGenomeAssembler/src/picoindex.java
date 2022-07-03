import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class picoindex {


	public static void constructBWT(String T, int[] sa, String[] BWT) {
		int len = sa.length; 
		
		//String[] BWT = new String[len]; 
		for (int i = 0; i < len; i++){ 
			if (sa[i] > 0) {
				
				BWT[i] = Character.toString(T.charAt(sa[i] - 1)); 
			} else if (sa[i] == 0) {
				
				BWT[i] = "$"; 
			}
		
		}		
		
	}

	public static int[][] constructOcc(String[] BWT) {
		int[][] Occ = new int[5][BWT.length];
         
        int k = 0; 
        for (String s : BWT) {
          
            for (int i = 0; i <= 4; i++) {
                Occ[i][k] = (k-1 >= 0) ? (Occ[i][k-1]) : 0; 
            }
            if (s.equals("A")) {
                Occ[1][k]++;
                
            } else if (s.equals("C")) {
                Occ[2][k]++;              
            } else if (s.equals("G")) {                
                Occ[3][k]++;
              
            } else if (s.equals("T")) {              
                Occ[4][k]++;
               
            } else if (s.equals("$")) {            
                Occ[0][k]++; 
               
            }
            k++;
        }
		return Occ; 
	}

	public static void main(String[] args) {
		String input = args[0];
		String output = args[1];

	
		StringBuffer genome_buf = new StringBuffer();

		try(BufferedReader br = new BufferedReader(new FileReader(input))) {
			for(String line; (line = br.readLine()) != null; ) {
				if (!line.startsWith(">")) {
					genome_buf.append(line.toUpperCase().strip());
				}
			}
			// line is not visible here.
		} catch (IOException e) {
			e.printStackTrace();
		}
		genome_buf.append("$");
		String genome = genome_buf.toString();
		int[] sa = SuffixArray.constructSuffixArray(genome);
		// System.out.println(genome); 
		//System.out.println(Arrays.toString(sa)); 
		//String[] F = new String[sa.length];
		String[] BWT = new String[sa.length];
		constructBWT(genome, sa, BWT); 
		//System.out.println(Arrays.toString(BWT)); 
		//String L = new String(); 
        int[][] Occ = constructOcc(BWT); 

		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(output));
			out.writeObject(sa);
			out.writeObject(BWT); 
		//	out.writeObject(F); 
			out.writeObject(Occ); 
		//	out.writeObject(L); 
			out.writeBytes(genome);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				System.err.println("Couldn't close " + output);
			}
		}

		
	}
	
}
