public class MineOpt {
	public MineOpt(String[] args) {
		if (args.length < 1) {
			usage();
		}
		String dimstr = args[0];
		int dimparse = 2;
		try {
			dimparse = java.lang.Integer.parseInt(dimstr);
		} catch (java.lang.Exception e) {
			dims = dimparse;
			usage();
			return;
		}
		dims = dimparse;
		if (dims < 1 || dims > 6) {
			System.out.println("Order out of range, please specify 1 <= d <= 6 dimensions");
			System.exit(1);
		}
	}
	public static void usage() {
		System.out.println("Usage: java MineAI <dimensions>");
		System.exit(1);
	}
	final int dims;
}
