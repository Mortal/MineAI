public abstract class Player {
	protected Minefield field;
	Player(Minefield field) {
		this.field = field;
	}
	abstract void play();
}
