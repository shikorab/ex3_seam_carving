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
}