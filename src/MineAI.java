public class MineAI {
	public static void main(String[] args) {
		
		MineOpt opts = new MineOpt(args);
		int[] size = opts.size.clone();
		Minefield field = new Minefield(size.length, size, opts.mines);
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
