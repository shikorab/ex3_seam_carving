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
		if (args.length != 5) {
			System.out.println("The program expect 5 arguments");
			return;
		}
		
		String in_img_path = args[0];
		int out_cols = Integer.parseInt(args[1]);
		int out_rows = Integer.parseInt(args[2]);
		boolean energy_type = Integer.parseInt(args[3]) != 0;
		String out_img_path = args[4];
		
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
		int in_cols = in_img.getWidth();
		int in_rows = in_img.getHeight();
		Matrix matrix = new Matrix(in_img);
		
		
		
			
		/*
		 * Calc output image
		 */
		int delta_rows = out_rows - in_rows;
		int delta_cols = out_cols - in_cols;
		
		/*add/remove cols cols*/
		updateCols(matrix, delta_cols);
		updateRows(matrix, delta_cols);
		
		
		
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

	private static void updateRows(Matrix matrix, int delta_cols) {
		matrix.transpose();
		updateCols(matrix, delta_cols);
		matrix.transpose();
	}

	private static void updateCols(Matrix matrix, int delta_cols) {
		
		if (delta_cols > 0) {
			/*add cols*/
			Energy energy = new Energy(matrix);
			List<Seam> seam_list = energy.getLowestSeams(delta_cols);
			
		} else {
			/*remove cols*/
			for (int n = 0; n < Math.abs(delta_cols); n++){
				/*Get lowest seam*/
				Energy energy = new Energy(matrix);
				Seam seam = energy.getLowestSeam();
				if (delta_cols < 0) {
					matrix.removeSeam(seam);
				} else  {
					matrix.addSeam(seam);
				}
			}
		}
		
	}

}
