public class MineAI {
	public static void main(String[] args) {
		MineOpt opts = new MineOpt(args);
		int[] size = new int[opts.dims];
		for (int i = 0; i < opts.dims; ++i) {size[i] = 5;}
		Minefield field = new Minefield(opts.dims, size);
		Game game = new Game(field);
		Player ply = new AIPlayer(game.getField());
		System.out.println(game.dimensions);
	}
}
