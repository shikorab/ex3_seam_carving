package ex3_seam_carving;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args) {
		/*
		 * Command line expects 5 arguments:
		 * <input image filename> = Full path to the input image
		 * <output # columns> = Number of columns of the resized output image
		 * <output # rows> = Number of rows of the resized output image
		 * <energy type> = A boolean argument where '0' = regular energy without entropy term, '1' = regular energy with entropy term and '2' = forward energy
		 * <output image filename> = Full path to the output image (where your program will write the output image to).
		 */
		if ((args.length != 5) && ((args.length != 6))) {
			System.out.println("The program expect 5/6 arguments");
			return;
		}
		
		String in_img_path = args[0];
		int outCols = Integer.parseInt(args[1]);
		int outRows = Integer.parseInt(args[2]);
		boolean energyType = Integer.parseInt(args[3]) != 0;
		String out_img_path = args[4];
		boolean forward;
		if (args.length == 6)
			forward = Integer.parseInt(args[5]) != 0;
		else
			forward = false;
		/*
		 * Input image
		 */
		
		BufferedImage in_img;
		try {
			in_img = ImageIO.read(new File(in_img_path));
		} catch (IOException e) {
			System.out.println("Failed to read input image: " + in_img_path);
			e.printStackTrace();
			return;
		}
		int inCols = in_img.getWidth();
		int inRows = in_img.getHeight();
		Matrix matrix = new Matrix(in_img);
		
		
		
			
		/*
		 * Calc output image
		 */
		int deltaRows = outRows - inRows;
		int deltaCols = outCols - inCols;
		
		/*add/remove cols cols*/
		updateCols(matrix, deltaCols, energyType, forward);
		updateRows(matrix, deltaRows, energyType, forward);
		
		
		
		/*
		 * Save output image
		 */
		BufferedImage img_out = matrix.matrix2RGB();
		try {
			ImageIO.write(img_out, "png", new File(out_img_path));
		} catch (IOException e) {
			System.out.println("Failed to save image: " + out_img_path);
			e.printStackTrace();
			return;
		}
		
		System.out.println("Done");
		
		

	}

	private static void updateRows(Matrix matrix, int deltaRows, boolean energyType, boolean forward) {
		matrix.transpose();
		updateCols(matrix, deltaRows, energyType, forward);
		matrix.transpose();
	}

	private static void updateCols(Matrix matrix, int deltaCols, boolean energyType, boolean forward) {
		
		if (deltaCols > 0) {
			/*add cols*/
			Energy energy = new Energy(matrix, energyType, forward);
			List<Seam> seamList = energy.getLowestSeams(deltaCols);
//			for (Seam seam: seamList) {
//				matrix.markSeam(seam);
//			}
			matrix.duplicateSeam(seamList);
			
		} else {
			/*remove cols*/
			for (int n = 0; n < Math.abs(deltaCols); n++){
				/*Get lowest seam*/
				Energy energy = new Energy(matrix, energyType, forward);
				Seam seam = energy.getLowestSeam();
				if (deltaCols < 0) {
					matrix.removeSeam(seam);
				} else  {
					matrix.addSeam(seam);
				}
			}
		}
		
	}

}
