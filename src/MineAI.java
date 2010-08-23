public class MineAI {
	public static void main(String[] args) {
		MineOpt opts = new MineOpt(args);
		Game game = new Game(opts.dims);
		System.out.println(game.dimensions);
	}
}
