public class MineAI {
	public static void main(String[] args) {
		MineOpt opts = new MineOpt(args);
		int[] size = new int[opts.dims];
		for (int i = 0; i < opts.dims; ++i) {size[i] = 5;}
		Minefield field = new Minefield(opts.dims, size, 10);
		System.out.println(field.renderASCII());
		Game game = new Game(field);
		System.out.println("The game is on, Freddie. Do you wanna play?");
		Player ply = new AIPlayer(game.getField());
		boolean boom = false;
		try {
			ply.play();
		} catch (BoomException e) {
			System.out.println("Boom!");
			boom = true;
			field.revealtiles = true;
			System.out.println(field.renderASCII());
		}
		if (!boom) {
			System.out.println("See you.");
			field.revealtiles = true;
			System.out.println(field.renderASCII());
		}
		//Test.allTests();
	}
}
