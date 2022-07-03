Author: Shadi Khalil
Date: 1/10/2022
Languages: Java

Before running either executable, run the bash shell with 
./build.sh

 This is a picoaligner, a toy genome assembler combining 
 concepts and structures from previous projects in Bioinformatic
 Algorithms. It combines the capability to perform exact pattern matching 
 with either a FM-Index or Suffix Array data structures and performs fitting 
 alignment on reads in order to reconstruct a genome. The main novel part of 
 this project is implementing a seeding and filtering heuristic 
 to decide where it should score reads for the alignment. By 
 seeding at highly specific locations, I am able to narrow down
 my mapping locations and test alignment only at those useful spots. 


Picoindex creates an index of the genome, I chose to use the FM-Index as 
construction required a Suffix Array anyway, this way I had access to both
structures. 

Input: A genome string in FASTA format, an output .bin file. 
Output: A serialized FM-Index of the string.

Picomap performs fitting alignment to produce k possible mapping locations
for a pattern against the genome. 

Input: The index created by picoindex, a file containing read patterns in FASTA format, 
a mismatch penalty (used as -m), a gap penalty (used as -g), and an output .map file

Output: For each read in the reads file, the output should contain 
read_name   num_alignments (k)
reference_start_pos_1   score   CIGAR_1
reference_start_pos_2   score   CIGAR_2
...
reference_start_pos_k   score   CIGAR_K



Examples: 
./picoindex omicron.fa index.bin
./picomap index.bin omicron_reads.fa 1 3 output.map

CIGAR format: Each symbol is preceeded by an integer representing the number of like operations in a row
"=" - match
"X" - mismatch
"D" - deletion from Y
"I" - insertion into Y

Example Output: 
simulated.12	4
734173	-21	1=1X4=2X5=1X2=1X69=1X2=1X10=
734300	-21	1=1X4=2X5=1X2=1X69=1X2=1X10=
735890	-6	86=1X2=1X10=
731120	-3	89=1X10=
