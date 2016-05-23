package ex3_seam_carving;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.List;
/**
 * Matrix is an 3D array (height X width X rgb)
 *
 */
public class Matrix {
	double[][][] matrix;
	int height;
	int width;

	/*Create matrix given an image*/ 
	public Matrix(BufferedImage img) {

		height = img.getHeight();
		width = img.getWidth();
		
		matrix = new double[height][width][3];
		double[] rgb;

		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				rgb = getPixelData(img, j, i);

				for(int k = 0; k < rgb.length; k++){
					matrix[i][j][k] = rgb[k];
				}

			}
		}
	}
	
	public Matrix(Matrix matrix) {
		this.matrix = matrix.matrix.clone();
		this.height = matrix.height;
		this.width = matrix.width;
	}
	
	private static double[] getPixelData(BufferedImage img, int x, int y) {
		int argb = img.getRGB (x, y);

		double rgb[] = new double[] {
				(argb >> 16) & 0xff, //red
				(argb >>  8) & 0xff, //green
				(argb      ) & 0xff  //blue
		};

		return rgb;
	}
	
	/*
	 * Producing a BufferedImage that can be saved as png from a byte array of RGB values.
	 */
	public BufferedImage matrix2RGB() {
		byte[] buffer = new byte[width * height * 3];
		
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				for(int k = 0; k < 3; k++){
					buffer[(i * width + j) * 3 + k] = (byte) matrix[i][j][k];
				}

							}
		}

		
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, false, false,
				Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		DataBufferByte db = new DataBufferByte(buffer, width * height);
		WritableRaster raster = Raster.createWritableRaster(sm, db, null);
		BufferedImage result = new BufferedImage(cm, raster, false, null);

		return result;
	}

	public double get(int i, int j, int k) {
		return matrix[i][j][k];
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void markSeam(Seam seam) {
		for (int h = 0; h < height; h++) {
			matrix[h][seam.get(h)][0] = 255; 
		}
	}

	public void removeSeam(Seam seam) {
		double[][][] tempM = new double[height][width - 1][3];

		for(int i = 0; i < height - 1; i++){
			int skip = 0;
			for(int j = 0; j < width - 1; j++){
				if (seam.get(i) != j){
					for(int k = 0; k < 3; k++){
						tempM[i][j - skip][k] = matrix[i][j][k];
					}
				}
				else{
					skip = 1;
				}
			}
		}
		
		width--;
		matrix = tempM;
	}

	public void addSeam(Seam seam) {
		// TODO Auto-generated method stub
		
	}

	public void transpose() {
		double[][][] tempM = new double[width][height][3];

		for(int i = 0; i < height - 1; i++){
			for(int j = 0; j < width - 1; j++){
				for(int k = 0; k < 3; k++){
					tempM[j][i][k] = matrix[i][j][k];
				}	
			}
		}
		
		int temp = width;
		width = height;
		height = temp;
		matrix = tempM;
	}

	public void duplicateSeam(List<Seam> seamList) {
		double[][][] tempM = new double[height][width + seamList.size()][3];
		int shift;
		for(int i = 0; i < height - 1; i++){
			shift = 0;
			for(int j = 0; j < width - 1; j++){
				/*Copy cell*/
				for(int k = 0; k < 3; k++){
					tempM[i][j + shift][k] = matrix[i][j][k];
				}
				
				/*Check if should be duplicated*/
				for (Seam seam : seamList) {
					if (seam.get(i) == j) {
						shift++;
						for(int k = 0; k < 3; k++){
							tempM[i][j + shift][k] = matrix[i][j][k];
						}
					}
				}
			}
		}
		
		
		width += seamList.size();
		matrix = tempM;
		
	}
	
	public double getGreyScale(int i, int j) {
		return matrix[i][j][0] * 0.2989 + matrix[i][j][1] * 0.5870 + matrix[i][j][0] * 0.1140; 
	}
}
