class Minefield {
	public final int dimensions;
	public Minefield(int dimensions, int[] size) {
		this.dimensions = dimensions;
		if (size.length != dimensions) {
			throw new IllegalArgumentException("Length of size-array not equal to number of dimensions");
		}
	}
}
