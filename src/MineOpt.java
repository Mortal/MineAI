//import java.util.Vector;
public class MineOpt {
	public MineOpt(String[] args) {
		if (args.length < 2) {
			System.out.println(args.length);
			usage();
		}
		int[] size = new int[args.length - 1];
		for (int i = 0; i < args.length; ++i) {
			String arg = args[i];
			int parse = 0;
			try {
				parse = java.lang.Integer.parseInt(arg);
			} catch (java.lang.Exception e) {
				usage();
				return;
			}
			if (parse <= 0) {usage(); return;}
			if (i == args.length-1) {
				mines = parse;
			} else {
				size[i] = parse;
			}
		}
		this.size = size;
		final int dims = this.size.length;
		if (dims < 1 || dims > 4) {
			System.out.println("Order out of range, please specify 1 <= d <= 4 dimensions");
			System.exit(1);
		}
	}
	public static void usage() {
		System.out.println("Usage: java MineAI <x> [<y> [<z> [<w>]]] <mines>");
		System.exit(1);
	}
	int[] size;
	int mines;
}
