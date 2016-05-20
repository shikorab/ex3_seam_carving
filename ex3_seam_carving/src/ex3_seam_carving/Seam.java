package ex3_seam_carving;

public class Seam {
	private int length;
	int[] seam;
	
	public Seam(int length) {
		this.length = length;
		seam = new int[this.length];
	}
	
	public void set(int i, int value) {
		seam[i] = value;
	}

	public int get(int h) {
		return seam[h];
	}

}
