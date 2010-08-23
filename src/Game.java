class Game {
	public final int dimensions;
	private Minefield field;
	public Game(Minefield field) {
		this.field = field;
		this.dimensions = field.dimensions;
	}
	public Minefield getField() {return this.field;}
}
