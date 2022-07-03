import java.io.*; 
import java.util.*;


public class picomap {

    public static class OPTCell {
        public int score; 
        public Parent parent; 
        public OPTCell(int score, Parent parent) {
            this.score = score; 
            this.parent = parent; 
        }
        public int getScore() {
            return this.score; 
        }
        public Parent getParent() {
            return this.parent; 
        }

        public void setScore(int score) {
            this.score = score; 
        }

        public void setParent(int i, int j) {
            this.parent = new Parent(i, j); 
        }
    }

    public static class Parent {
        int i, j; 
        public Parent(int i, int j) {
            this.i = i; 
            this.j = j; 
        }

        public String toString() {
            return "("+ i + ", " + j + ")"; 
        }
    }


    private static int m, g; 
    private static String genome;  
    private static String[] bwt; 
    private static int[] sa; 
    private static int[][] Occ;
    static HashMap<String, Integer> ALPHABET = new HashMap<String, Integer>();
     
    static FileWriter outWriter; 




   public static void locate(int[] hits, int[] sa, int top, int bottom) {
        int i = 0; 
        // if (top == bottom) {
        //     hits[i] = sa[top]; 
        // } else {
            for (int j = top; j <= bottom; j++) {
            //     System.out.println("Just like before"); 
            //  System.out.println("sa[j + 1] " + sa[j + 1]); 
                
                hits[i++] = sa[j - 1];
            }
        // }
    }

    public static int rank(int[][] Occ, String c, int row) {
        if (row == 0) {
            return 0; 
        }
        return Occ[ALPHABET.get(c)][row - 1]; 
        // return Occ[ALPHABET.get(c)][row] - 1; 
    }


    public static int[] computeC(int[][] Occ, int k) {
        int[] C = new int[5]; 
        int tmp = 0;
        C[0] = 0; 
        int j = 0;  
        for (int i = 1; i < 5; i++) {
            C[i] = tmp + Occ[j++][k]; 
            tmp = C[i]; 
        }

        return C; 
    }

    public static int[] match(String pattern, String[] bwt, int[] C, int[][] Occ, int[] sa, String mode) throws IOException {
        int top = 1; 
        int bottom = bwt.length; 
        int i = pattern.length() - 1;
        int matchLen = 0; 
        int prev_top = top; 
        int prev_bot = bottom; 
        int longest_partial = 0; 
                   
        // System.out.println("[" + top + ", " + bottom + "]"); 
                
        while (i >= 0 && top <= bottom) {
            String c = Character.toString(pattern.charAt(i)); 
            if (mode.equals("partial")) {
                prev_top = top; 
                prev_bot = bottom; 
            }
                 
            top = C[ALPHABET.get(c)] + rank(Occ, c, top - 1) + 1;
            bottom = C[ALPHABET.get(c)] + rank(Occ, c, bottom);
              
            matchLen++;
            i--; 
        }

           
            
        int count = bottom + 1 - top;
           
        int[] hits = new int[count]; 
            // System.out.println("hits.length " + count); 
        if (count > 0) {
            locate(hits, sa, top, bottom); 
        }
        int k = hits.length; 
        if (mode.equals("complete")) {
            if (k == 0 || matchLen != pattern.length()) {
                    //System.out.println("no hits"); 
                matchLen = 0; 
            }
        } else if (mode.equals("partial") && (matchLen != pattern.length() || (matchLen == pattern.length() && top > bottom))) {
                // System.out.println(prev_top + ", " + prev_bot); 
            count = prev_bot + 1 - prev_top;
                // System.out.println("[prev_top, prev_bot]: [" + prev_top + ", " + prev_bot + "]");
            hits = new int[count];
            k = hits.length;
                
            if (count > 0) {
                locate(hits, sa, prev_top, prev_bot); 
            }
            matchLen = matchLen - 1; 
        }
        //System.out.println("hits: " + Arrays.toString(hits));
        // outWriter.write("\t" + k + "\n"); 
           
        // for (int index : hits) {
        //     //outWriter.write(index + "\t");
        //     align(index, pattern, genome.substring(index));  // min(end of pattern, end of genome)
        // }
        return hits;
    }


 
    
  
    // static String output; 
    static OPTCell[][] OPT; 
    // static Parent[][] parents; 

    
    private static int max(int i, int j) {
        return (i >= j) ? i : j; 
    }
    private static int max(int i, int j, int k) {
        int ret = max(i, j); 
        return max(ret, k); 
    }

    public static int score(String X, String Y, int i, int j) {
        char x_j = X.charAt(j - 1); 
        char y_i = Y.charAt(i - 1); 

        if (x_j == y_i) {
            return 0; 
        } else {
            return m; // mismatch cost
        }
    }

    // public Parent getParent(int i, int j) {
    //     return parents[i][j]; 
    // }

    public static OPTCell[][] computeOPT(String X, String Y, int x_len, int y_len) {
        OPT = new OPTCell[y_len + 1][x_len + 1]; 
        // parents = new Parent[y_len + 1][x_len + 1]; 
        OPT[0][0] = new OPTCell(0, new Parent(0, 0)); 
        // parents[0][0] = new Parent(0, 0); 
       
        for (int i = 1; i <= y_len; i++) {
            OPT[i][0] = new OPTCell(0, new Parent(i - 1, 0)); 
                // parents[i][0] = new Parent(i - 1, 0); 
                
        }
        for (int j = 1; j <= x_len; j++) { 
            OPT[0][j] = new OPTCell(j * g, new Parent(0, j - 1)); 
                // parents[0][j] = new Parent(0, j - 1);  
        }
        

        for (int i = 1; i <= y_len; i++) {
            for (int j = 1; j <= x_len; j++) {
                //OPT(i,j)
                int match = score(X, Y, i, j) + (OPT[i - 1][j - 1]).getScore(); 
                int mismatch_ai = (OPT[i - 1][j]).getScore() + g; 
                int mismatch_bj = (OPT[i][j - 1]).getScore() + g; 
                int max = max(match, mismatch_ai, mismatch_bj); 
                
                OPT[i][j] = new OPTCell(max, null); 
                // OPT[i][j] = max; 
                // OPT[i][j].setScore(max); 
                if (max == match) {
                    OPT[i][j].setParent(i - 1, j - 1); 
                    // parents[i][j] = new Parent(i - 1, j - 1); 
                    // continue; 

                } else if (max == mismatch_ai) {
                    OPT[i][j].setParent(i - 1, j); 
                    // parents[i][j] = new Parent(i - 1, j);
                    // continue;  

                } else if (max == mismatch_bj) {
                    OPT[i][j].setParent(i, j - 1); 
                    // parents[i][j] = new Parent(i, j - 1); 
                    // continue; 
                }

            }
        }


        // for (int i = y_len; i >= 0; i--) { 
        //     for (int j = 0; j <= x_len; j++) {
        //         System.out.print(OPT[i][j] + "\t"); 
        //     }
        //     System.out.println();
            
        // }

        // for (int i = y_len; i >= 0; i--) {
        //     for (int j = 0; j <= x_len; j++) {
        //         System.out.print(parents[i][j].toString() + "\t"); 
        //     }
        //     System.out.println(); 
        // }

        return OPT;
    }

    public static String parseCigar(String cigar) {
        String curr_symbol = Character.toString(cigar.charAt(0));
        String prev_symbol = Character.toString(cigar.charAt(0));
        String out = "";  
        int count = 0; 
        for (int i = 0; i < cigar.length(); i++) {
            curr_symbol = Character.toString(cigar.charAt(i)); 
            if (curr_symbol.equals(prev_symbol)) {
                count++; 
            } else {
                out = out.concat(Integer.toString(count)).concat(prev_symbol); 
                count = 1; 
                prev_symbol = curr_symbol; 
            }
        }
        out = out.concat(Integer.toString(count)).concat(curr_symbol); 
        return out; 

    }


    public static void printOutput(int index, int score, String cigar) {
        try{
            outWriter.write(index + "\t");
            outWriter.write(score + "\t" + cigar + "\n"); 
        } catch (IOException e) {
            e.printStackTrace(); 
        }
        
    }


    
    public static void fittingAlignment(int index, String X, String Y, int m, int g) {
            
            int x_len = OPT[0].length;
            int y_len = OPT.length; 
            int start_i = 0; 
            int start_j = x_len - 1; 
            int score = -99999;
            String cigar = ""; 
        
  
            for (int i = 0; i < y_len; i++) {
                if (OPT[i][start_j].getScore() > score) {
                    start_i = i; 
               
                    score = OPT[i][start_j].getScore(); 
                } 
            }
       
       
            int parent_i = OPT[start_i][start_j].getParent().i; 
            int parent_j = OPT[start_i][start_j].getParent().j;

            if (parent_i == start_i - 1 && parent_j == start_j - 1) { // diag
                if (OPT[start_i][start_j].getScore() == OPT[parent_i][parent_j].getScore()) {
                    cigar = cigar.join("", "=", cigar); 
                } else {
                    cigar = cigar.join("", "X", cigar); 
                }
            } else if (parent_i == start_i - 1 && parent_j == start_j) { // Down
                cigar = cigar.join("", "D", cigar); 
            } else if (parent_i == start_i && parent_j == start_j - 1) { // Left
                cigar = cigar.join("", "I", cigar);
            } 
            
    //    System.out.println("Start Max, (i, j): " + score + ", (" + start_i + ", " + start_j + ")");

            int i = OPT[start_i][start_j].getParent().i;
            int j = OPT[start_i][start_j].getParent().j; 
    //    System.out.println("(i, j): (" + i  + ", " + j+ ")");


            while (j > 0) { // Maybe just i > 0, j > 0
            
            // score += OPT[i][j]; 
                int tmp_i = i; 
                int tmp_j = j;
                i = OPT[tmp_i][tmp_j].getParent().i; 
                j = OPT[tmp_i][tmp_j].getParent().j; 
            // System.out.println("(i, j): (" + i  + ", " + j+ ")");
                if (i == tmp_i - 1 && j == tmp_j - 1) { // diag
                    if (OPT[tmp_i][tmp_j].getScore() == OPT[i][j].getScore()) {
                        cigar = cigar.join("", "=", cigar); 
                    } else {
                        cigar = cigar.join("", "X", cigar); 
                    }
                } else if (i == tmp_i - 1 && j == tmp_j) { // Down
                    cigar = cigar.join("", "D", cigar); 
                } else if (i == tmp_i && j == tmp_j - 1) { // Left
                    cigar = cigar.join("", "I", cigar);
                } 
            }

            cigar = parseCigar(cigar); 
            //    System.out.println(cigar); 
            //    System.out.println(); 
      

            printOutput(index, score, cigar); 
        
      
    }



    public static void align(int index, String X, String Y) {
        // System.out.println("X: " + X); 
        // System.out.println("Y: " + Y); 
        // System.out.println("m, g: "  + m + ", " + g); 
        // System.out.println("g: " + g); 
        OPT = computeOPT(X, Y, X.length(), Y.length()); 
        
        fittingAlignment(index, X, Y, m, g); 

        
    }

    /** Probably inefficient candidate filtering */
    public static HashSet<Integer> candidateFilter(String name, int[] hits1, int[] hits2, int[] hits3, int[] hits4, int quart_len) {
        // System.out.println("1: " + Arrays.toString(hits1)); 
        // System.out.println("2: " + Arrays.toString(hits2)); 
        // System.out.println("3: " + Arrays.toString(hits3)); 
        // System.out.println("4: " + Arrays.toString(hits4)); 



        HashSet<Integer> starts = new HashSet<Integer>(); 
     
        
        for (int i = 0; i < hits1.length; i++) {
            if (name.equals("simulated.66")) {
            System.out.println(hits1[i]); 
            }
             int count = 0; 
            for (int j = 0; j < hits2.length; j++) {
                // if (hits[i] + (quart_len) == hits2[j]) {
                //     starts.add(hits1[i]); 
                // }
               

                if (hits1[i] + (quart_len) == hits2[j] ) {
                // && hits1[i] + (quart_len)  + 15 > hits2[j]) { 
                    int to_add = hits1[i]; 
                    boolean dont_do_it = check_similar_indices(to_add, starts); 
                    if (!dont_do_it) {
                    // starts.add(hits1[i]); 
                    count++; 
                    if (count > 0) {
                         starts.add(hits1[i]); 
                    }
                    }
                } 
            }

            for (int j = 0; j < hits3.length; j++) {
                if (hits1[i] + (2 * (quart_len))  == hits3[j]) { 
                // && hits1[i] + (2 * (quart_len))  + 15 > hits3[j]) { 
                    int to_add = hits1[i]; 
                    boolean dont_do_it = check_similar_indices(to_add, starts); 
                    if (!dont_do_it) {
                    // starts.add(hits1[i]); 
                     count++; 
                    if (count > 1) {
                        starts.add(hits1[i]); 
                    }
                    }
                } 
            }

            for (int j = 0; j < hits4.length; j++) {
                if (hits1[i] + (3 * (quart_len)) == hits4[j] ) {
                // && hits1[i] + (3/4 * (quart_len))  + 15 > hits4[j]) { 
                    int to_add = hits1[i]; 
                    boolean dont_do_it = check_similar_indices(to_add, starts); 
                    if (!dont_do_it) {
                    // starts.add(hits1[i]); 
                    count++; 
                    // if (name.equals("simulated.66")) {
                    //     // System.out.println("Yes Chef");
                    //         System.out.println(count); 
                    // }
                    if (count > 0) {
                        starts.add(hits1[i]);
                    }
                    }
                } 
            }
            
        }

        for (int i = 0; i < hits2.length; i++) {
            int count = 0; 
            for (int j = 0; j < hits3.length; j++) {
                if (hits2[i] + (quart_len) == hits3[j]) { 
                // && hits2[i] + (quart_len)  + 15 > hits3[j]) { 
                    
                    int to_add = hits2[i] - (quart_len); 
                    boolean dont_do_it = check_similar_indices(to_add, starts); 
                    if (!dont_do_it) {
                        count++; 
                        if (count > 0) {
                    starts.add(hits2[i] - quart_len); 
                        }
                    }
                } 
            }

            for (int j = 0; j < hits4.length; j++) {
                if (hits2[i] + (2 * (quart_len))  == hits4[j]){ 
                // && hits2[i] + (2 * (quart_len))  + 15 > hits4[j]) { 

                    int to_add = hits2[i] - (quart_len); 
                    boolean dont_do_it = check_similar_indices(to_add, starts); 
                    if (!dont_do_it) {
                        count++; 
                        // if (name.equals("simulated.39")) {
                        //     System.out.println(count); 
                        // }
                        if (count >= 1) {
                    starts.add(hits2[i] - quart_len); 
                        }
                        
                    }
                } 
            }
            
        }

        for (int i = 0; i < hits3.length; i++) {
            int count = 0; 
            for (int j = 0; j < hits4.length; j++) {
                if (hits3[i] + (quart_len)  == hits4[j] ){
                // && hits3[i] + (quart_len)  + 15 > hits4[j]) { 
                    
                    int to_add = hits3[i] - (2 * quart_len); 
                    boolean dont_do_it = check_similar_indices(to_add, starts); 
                    if (!dont_do_it) {
                        count++; 
                        if (count >= 1) {
                    starts.add(hits3[i] - (2 * quart_len)); 
                        }
                    }
                } 
            }
        }
        

        // System.out.println("Starts: " + starts.toString()); 

        return starts; 
    }
    
    public static boolean check_similar_indices(int to_add, HashSet<Integer> set) {
        boolean out = false; 
        int[] similars = new int[31]; 
        int j = 0; 
        for (int i = -15; i <= 15; i++) {
            similars[j++] = to_add + i; 
        }
        

        for (int index : similars) {
            if (set.contains(index)) { 
                out = true; 
            }
        }

        return out; 
    }

   

    public static void main(String[] args) {
      String index_file = args[0]; 
        String read_file = args[1]; 
        int mismatch_penalty = Integer.parseInt(args[2]); 
        int gap_penalty = Integer.parseInt(args[3]); 
        String output_file = args[4]; 

        m = -mismatch_penalty;
        g = -gap_penalty;

        ObjectInputStream in = null;
		String genome_in = null;
		int[] sa_in = null;
        String[] bwt_in = null; 
       // String[] f_in = null; 
        int[][] Occ_in = null;

        ALPHABET.put("$", 0); 
        ALPHABET.put("A", 1); 
        ALPHABET.put("C", 2); 
        ALPHABET.put("G", 3);
        ALPHABET.put("T", 4); 

       // String L = null; 
		try {
			in = new ObjectInputStream(new FileInputStream(index_file));
			sa_in = (int[]) in.readObject();
            bwt_in = (String[]) in.readObject(); 
          //  f_in = (String[]) in.readObject(); 
            Occ_in = (int[][]) in.readObject(); 
          
            byte[] genomeBytes = new byte[sa_in.length];
			in.readFully(genomeBytes);
			genome_in = new String(genomeBytes);
            
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				System.err.println("Couldn't close " + index_file);
			}
		}

        //System.out.print(Arrays.toString(sa_in)); 
       


        try {

            // FileWriter saWriter = new FileWriter(new File("sa.txt")); 
            // saWriter.write(Arrays.toString(sa_in)); 
            // saWriter.close();
            outWriter = new FileWriter(new File(output_file));
        
            int[] C = computeC(Occ_in, bwt_in.length - 1); 
            // System.out.println(Arrays.toString(C)); 
            // System.out.println(Arrays.toString(sa_in)); 
        
            boolean first = true; 
            String name = new String(); 
            String pattern = new String(); 
            genome = genome_in; 
            // Y = genome; 
            int count = 0; 
            try (Scanner sc = new Scanner(new File(read_file))) {
                while (sc.hasNextLine()) {
                    
                    String line = sc.nextLine().trim(); 
                    if (line.startsWith(">")) {
                        count++; 
                        if (first) {
                            first = false;
                        } else {
                            //System.out.println(); 
                        }
                        name = line.substring(1); 

                    } else {
                    //query the pattern string (line) here; 
                        pattern = line;
                        // X = pattern;
                        outWriter.write(name + "\t"); 

                        String r1 = pattern.substring(0, pattern.length()/4); 
                        String r2 = pattern.substring(pattern.length()/4, pattern.length()/2); 
                        String r3 = pattern.substring(pattern.length()/2, pattern.length() * 3/4);
                        String r4 = pattern.substring(pattern.length() * 3/4, pattern.length());

                        
                        
                        int[] hits1 = match(r1, bwt_in, C, Occ_in, sa_in, "partial"); 
                        int[] hits2 = match(r2, bwt_in, C, Occ_in, sa_in, "partial"); 
                        int[] hits3 = match(r3, bwt_in, C, Occ_in, sa_in, "partial"); 
                        int[] hits4 = match(r4, bwt_in, C, Occ_in, sa_in, "partial"); 
                        //match(pattern, bwt_in, C, Occ_in, sa_in, "patrial");
                        // outWriter.write("\n"); 

                        

                        HashSet<Integer> valid_hits = candidateFilter(name, hits1, hits2, hits3, hits4, pattern.length()/4); 

                        if (name.equals("simulated.34") ) {//|| name.equals("simulated.12") || name.equals("simulated.39") || name.equals("simulated.66")) {
                            System.out.println("1: " + Arrays.toString(hits1)); 
                            System.out.println("2: " + Arrays.toString(hits2)); 
                            System.out.println("3: " + Arrays.toString(hits3)); 
                            System.out.println("4: " + Arrays.toString(hits4)); 
                            System.out.println("Valid: " + valid_hits.toString()); 
                        }

                        outWriter.write(valid_hits.size() + "\n");
                        for (Integer index : valid_hits) {
                            // outWriter.write("\n"); 
                            align(index, pattern, genome.substring(index - 15, (Math.min((index + 115), genome.length())))); 
                        }
                        
                    
                    }
                }
                // System.out.println(count); 
            } catch (FileNotFoundException e) {
                System.out.println("Reference File Not Found"); 
            }
            outWriter.close(); 
		
        } catch (IOException e) {
            e.printStackTrace(); 
        }

    }    
            

           
    
}



