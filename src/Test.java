class Test {
	static void ass(boolean e) {
		if (!e) {
			throw new RuntimeException("Assertion failed");
		}
	}
	static void assEq(int got, int expect) {
		if (got != expect) {
			throw new RuntimeException("Got "+got+", expected "+expect);
		}
	}
	static void allTests() {
		testMinefield();
		allpositions();
	}
	static void testMinefield() {
		testMinefield_orderAssert();
		testMinefield_posToId();
		testMinefield_pow();
		testMinefield_neighborIdOffsets();
	}
	static void testMinefield_orderAssert() {
		boolean ok = false;
		try {
			int[] coordinates = {1};
			Minefield.orderAssert(2, coordinates);
		} catch (WrongOrderException e) {
			ok = true;
		}
		ass(ok);
	}
	static void testMinefield_posToId() {
		testMinefield_posToId2D(0,0, 1,1, 0);
		testMinefield_posToId2D(0,1, 1,2, 1);
		testMinefield_posToId2D(1,1, 2,2, 3);
		testMinefield_posToId2D(1,1, 4,4, 5);
		testMinefield_posToId3D(0,0,0, 1,1,1, 0);
		testMinefield_posToId3D(1,1,1, 2,2,2, 7);
		boolean ok = false;
		try {
			testMinefield_posToId3D(1,2,3, 10,10,10, 124);
		} catch (RuntimeException e) {
			ok = true;
		}
		ass(ok);
	}
	static void testMinefield_posToId2D(int pos1, int pos2, int size1, int size2, int result) {
		//System.out.println("posToId2D test: "+pos1+","+pos2+" ("+size1+","+size2+") = "+result);
		int[] pos = {pos1, pos2};
		int[] size = {size1, size2};
		assEq(Minefield.posToId(pos, size), result);
	}
	static void testMinefield_posToId3D(int pos1, int pos2, int pos3, int size1, int size2, int size3, int result) {
		//System.out.println("posToId3D test: "+pos1+","+pos2+","+pos3+" ("+size1+","+size2+","+size3+") = "+result);
		int[] pos = {pos1, pos2, pos3};
		int[] size = {size1, size2, size3};
		ass(Minefield.posToId(pos, size) == result);
	}
	static void allpositions() {
		int[] pos = {0,0,0,0};
		int[] size = {4,5,6,7};
		allpositions(pos, size, 0, 0);
	}
	static int allpositions(int[] pos, int[] size, int nextID, int dim) {
		if (dim == size.length) {
			int id = Minefield.posToId(pos, size);
			System.out.println(nextID+"="+id);
			//assEq(id, nextID);
			return nextID+1;
		}
		for (int i = 0; i < size[dim]; ++i) {
			pos[dim] = i;
			nextID = allpositions(pos, size, nextID, dim+1);
		}
		return nextID;
	}
	static void testMinefield_pow() {
		assEq(Minefield.pow(2,10),1024);
		assEq(Minefield.pow(1,100),1);
		assEq(Minefield.pow(42,0),1);
	}
	static void testMinefield_neighborIdOffsets() {
		/*
		int[] size = {3,3,3,3};
		int[] offs = Minefield.neighborIdOffsets(size);
		String s = "";
		for (int i = 0, l = offs.length; i < l; ++i) {
			s += offs[i]+" ";
		}
		System.out.println("neighborIdOffsets({3,3,3,3}) returns "+s+"- does that seem reasonable?");
		*/
	}
}
