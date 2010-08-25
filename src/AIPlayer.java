class AIPlayer extends Player {
	AIPlayer(Minefield field) {
		super(field);
	}
	void play() {
		pressFirst();
	}
	void pressFirst() {
		int[] size = field.size;
		int[] pos = new int[size.length];
		for (int i = 0; i < pos.length; ++i) {
			pos[i] = size[i]/2;
		}
		field.getTile(pos).press();
	}
}
