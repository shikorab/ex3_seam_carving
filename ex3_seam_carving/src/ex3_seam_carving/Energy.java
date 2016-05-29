package ex3_seam_carving;

import java.util.LinkedList;
import java.util.List;

public class Energy {
	private Matrix matrix;
	private double[][] f_mat;
	private double[][] p_mat;
	private boolean energyType;
	private boolean forward;
	
	public Energy(Matrix matrix, boolean energyType, boolean forward) {
		this.matrix = new Matrix(matrix);
		this.energyType = energyType;
		this.forward = forward;
	}
	
	/**
	 * Calc lowest seam energy
	 * 
	 */
	public Seam getLowestSeam() {
		double[] cArr = {0, 0, 0};
		int height = matrix.getHeight();
		int width = matrix.getWidth();
		double[][] M = new double[height][width];
		
		/* Calc greyscale and normalized matrix */
		/*Entropy*/
		if (energyType) {
			calc_f_mat();
			calc_p_mat();
		}
		
		/*Calc energy of first line*/
		for (int w = 0; w < width; w++) {
			M[0][w] = pixelEnergy(0, w);
		}
		
		/*Calc the rest of the energy matrix*/
		for (int h = 1; h < height; h++) {
			if (forward){
				forwardEnergy(h, 0, cArr);
			}
			M[h][0] = pixelEnergy(h, 0) + Math.min(M[h-1][1]+ cArr[1], M[h-1][0] + cArr[2]);
			for (int w = 1; w < width - 1; w++) {
				if (forward){
					forwardEnergy(h, w, cArr);
				}
				
				M[h][w] = pixelEnergy(h, w) + Math.min(
														M[h-1][w - 1] + cArr[0], 
														Math.min(M[h-1][w] + cArr[1], M[h-1][w +1] + cArr[2]));
			}
			if (forward){
				forwardEnergy(h, width - 1, cArr);
			}
			M[h][width - 1] = pixelEnergy(h, width - 1) + Math.min(M[h-1][width - 1] + cArr[0], M[h-1][width - 2] + cArr[1]);
		}
		
		/*Calc Seam*/
		Seam seam = new Seam(height);
		/*Find the lowest energy in last line*/
		double value = Double.MAX_VALUE;
		int minwidth = -1;
		for (int w = 0; w < width; w++) {
			if (M[height - 1][w] < value) {
				value = M[height - 1][w];
				minwidth = w;
			}
		}
		seam.set(height - 1, minwidth);
		
		/*Calc rest of the seam starting from (height - 1, minwidth)*/
		for (int h = height - 2; h >= 0; h--) {
			value = Double.MAX_VALUE;
			int prev_min = minwidth;
			minwidth = -1;
			for (int w = Math.max(0, prev_min - 1); 
					w < Math.min(width - 1, prev_min + 1); 
					w++) 
			{
				if (M[h][w] < value) {
					value = M[h][w];
					minwidth = w;
				}
			}
			seam.set(h, minwidth);
		}
		
		return seam;
	}
	
	/**
	 * Calc pixel (i, j) forward energy
	 * 
	 */
	private void forwardEnergy(int i, int j, double[] cArr) {
		double CL = 0;
		double CU = 0;
		double CR = 0;
		
		int width = matrix.getWidth();
		
		for (int k = 0; k < 3; k++){			
			if ((j + 1 < width) && (j - 1 >= 0)){
				double temp = Math.abs(matrix.get(i, j + 1, k) - matrix.get(i, j - 1, k));
				CL += temp;
				CU += temp;
				CR += temp;
			}
			if ((i - 1 >= 0) && (j - 1 >= 0)){
				CL += Math.abs(matrix.get(i - 1, j, k) - matrix.get(i, j - 1, k));
			}
			if ((i - 1 >= 0) && (j + 1 < width)){
				CR += Math.abs(matrix.get(i - 1, j, k) - matrix.get(i, j + 1, k));
			}
			
		}
		
		/* Store the C values in array */
		cArr[0] = CL/3.0;
		cArr[1] = CU/3.0;
		cArr[2] = CR/3.0;
	
	}

	/**
	 * Calc pixel (i, j) pixelEnergy
	 * 
	 */
	private double pixelEnergy(int i, int j) {
		int h = matrix.getHeight();
		int w = matrix.getWidth();
		
		double count = 0;
		double energy = 0;
		
		for (int k = 0; k < 3; k++) {
			if (i - 1 >= 0 && j - 1 >= 0) {
				energy += Math.abs(matrix.get(i, j, k) - matrix.get(i-1, j-1, k));
				count++;
			}
			if (i - 1 >= 0) {
				energy += Math.abs(matrix.get(i, j, k) - matrix.get(i-1, j, k));
				count++;
			}
			if (j - 1 >= 0) {
				energy += Math.abs(matrix.get(i, j, k) - matrix.get(i, j-1, k));
				count++;
			}
			if (i + 1 < h && j + 1 < w) {
				energy += Math.abs(matrix.get(i, j, k) - matrix.get(i+1, j+1, k));
				count++;
			}
			if (i + 1 < h) {
				energy += Math.abs(matrix.get(i, j, k) - matrix.get(i+1, j, k));
				count++;
			}
			if (j + 1 < w) {
				energy += Math.abs(matrix.get(i, j, k) - matrix.get(i, j+1, k));
				count++;
			}
			if (i + 1 < h && j - 1 >= 0) {
				energy += Math.abs(matrix.get(i, j, k) - matrix.get(i+1, j-1, k));
				count++;
			}
			if (i - 1 >= 0 && j + 1 < w) {
				energy += Math.abs(matrix.get(i, j, k) - matrix.get(i-1, j+1, k));
				count++;
			}
		}
		
		double result =  energy / count;
		
		/*Entropy*/
		if (energyType) {
			result += getH(i, j);
			result /= 2.0;
		}
		
		return result;
	}
	
	private void calc_f_mat(){
		f_mat = new double[matrix.getHeight()][matrix.getWidth()];
		
		for (int m = 0; m < matrix.getHeight(); m++) {
			for (int n = 0; n < matrix.getWidth() ; n++) {
				f_mat[m][n] = matrix.getGreyScale(m, n);
			}	
		}
	}
	
	private void calc_p_mat(){
		p_mat = new double[matrix.getHeight()][matrix.getWidth()];
		
		for (int i = 0; i < matrix.getHeight(); i++) {
			for (int j = 0; j < matrix.getWidth() ; j++) {
				p_mat[i][j] = 0;
				int count = 0;
				for (int m = Math.max(0, i - 4); m < Math.min(matrix.getHeight() - 1, i + 4); m++) {
					for (int n = Math.max(0, j - 4); n < Math.min(matrix.getWidth() - 1, j + 4); n++) {
						p_mat[i][j] += f_mat[m][n];
						count++;
					}	
				}
				p_mat[i][j] = f_mat[i][j]/(p_mat[i][j] * 81.0 / count);
			}	
		}
	}
	
	private double getH(int i, int j) {
		double res = 0;
		int count = 0;
		for (int m = Math.max(0, i - 4); m < Math.min(matrix.getHeight() - 1, i + 4); m++) {
			for (int n = Math.max(0, j - 4); n < Math.min(matrix.getWidth() - 1, j + 4); n++) {
				double P = p_mat[m][n];
//				System.out.println("P: "+ P);
				res += P * Math.log(P);
				count++;
//				System.out.println("res: "+ res);
			}	
		}
//		System.out.println("total res: " + res);
		return - res * 81.0 / count;
	}
	
	
	public List<Seam> getLowestSeams(int deltaCols) {
		
		List<Seam> seamsList = new LinkedList<Seam>();
		for (int seamIndex = 0; seamIndex < deltaCols; seamIndex++) {
			Seam seam = getLowestSeam();
			/*Update seam index*/
			for (int h = 0; h < matrix.getHeight(); h++) {
				int count = 0;
				for (Seam s : seamsList) {
					if (s.get(h) <= seam.get(h)) {
						count++;
					}
				}
				seam.set(h, seam.get(h) + count);
			}
			seamsList.add(seam);
			matrix.removeSeam(seam);
		}
		return seamsList;
	}
}
