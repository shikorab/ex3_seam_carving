package ex3_seam_carving;

import java.util.LinkedList;
import java.util.List;

public class Energy {
	private Matrix matrix;
	private boolean energyType;
	
	public Energy(Matrix matrix, boolean energyType) {
		this.matrix = new Matrix(matrix);
		this.energyType = energyType;
	}
	
	/**
	 * Calc lowest seam energy
	 * 
	 */
	public Seam getLowestSeam() {
		int height = matrix.getHeight();
		int width = matrix.getWidth();
		double[][] M = new double[height][width];
		
		/*Calc energy of first line*/
		for (int w = 0; w < width; w++) {
			M[0][w] = pixelEnergy(0, w);
		}
		
		/*Calc the rest of the energy matrix*/
		for (int h = 1; h < height; h++) {
			M[h][0] = pixelEnergy(h, 0) + Math.min(M[h-1][1], M[h-1][0]);
			for (int w = 1; w < width - 1; w++) {
				M[h][w] = pixelEnergy(h, w) + Math.min(
														M[h-1][w - 1], 
														Math.min(M[h-1][w], M[h-1][w +1]));
			}
			M[h][width - 1] = pixelEnergy(h, width - 1) + Math.min(M[h-1][width - 1], M[h-1][width - 2]);
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
	 * Calc pixel(i, j) energy
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
		}
		
		return result;
	}
	private double getP(int i, int j) {
		int count = 0;
		for (int m = Math.max(0, i - 4); m < Math.min(matrix.getHeight() - 1, i + 4); m++) {
			for (int n = Math.max(0, j - 4); n < Math.min(matrix.getWidth() - 1, j + 4); n++) {
				count += matrix.getGreyScale(m, n);
			}	
		}
		
		return matrix.getGreyScale(i, j) / count;
	}
	
	private double getH(int i, int j) {
		int count = 0;
		//System.out.println("i: " + i + " j: " + j);
		for (int m = Math.max(0, i - 4); m < Math.min(matrix.getHeight() - 1, i + 4); m++) {
			for (int n = Math.max(0, j - 4); n < Math.min(matrix.getWidth() - 1, j + 4); n++) {
				double P = getP(m, n);
				//System.out.println("m: " + m + " n: " + n);
				//System.out.println("P: " + P);
				count += P * Math.log(P);
				//System.out.println("count: " + count);
			}	
		}
		
		return - count;
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
