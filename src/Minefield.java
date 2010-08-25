import java.util.Random;
import java.util.Vector;
class Minefield {
	public final int dimensions;
	public final int[] size;
	private Tile[] tiles;
	public final int tilecount;
	public final int bombcount;
	private int flagcount;
	private Random rand;
	public boolean revealtiles;

	/* static implementations of some useful functions
	 * for ease of testing
	 */
	static void orderAssert(int dimensions, int[] coordinates) {
		if (coordinates.length != dimensions) {
			throw new WrongOrderException();
		}
	}
	static int posToId(int[] pos, int[] size) {
		final int dimensions = size.length;
		orderAssert(dimensions, pos);
		int id = 0;
		for (int i = 0; i < dimensions; ++i) {
			id *= size[i];
			id += pos[i];
		}
		return id;
	}
	/* TODO
	static int[] idToPos(int id, int[] size) {
		final int dimensions = size.length;
		int pos[] = new int[dimensions];
	}
	*/
	static int pow(int base, int exponent) {
		int result = 1;
		for (int i = 0; i < exponent; ++i) {
			result *= base;
		}
		return result;
	}
	static int[][] neighborPositions(int[] pos, int[] size) {
		final int dimensions = size.length;
		final int maxneighbors = pow(3, dimensions)-1;
		orderAssert(dimensions, pos);
		//System.out.println(neighbors);
		Vector<int[]> result = new Vector<int[]>(maxneighbors);
		neighborPositions(pos.clone(), size, 0, result);
		int[][] arrayRes = new int[result.size()][dimensions];
		arrayRes = result.toArray(arrayRes);
		return arrayRes;
	}
	static void neighborPositions(int[] pos, int[] size, final int idx, Vector<int[]> result) {
		final int dimensions = size.length;
		if (idx >= dimensions) {return;}
		final int idx2 = idx+1;
		if (--pos[idx] >= 0) {
			result.add(pos.clone());
			neighborPositions(pos.clone(), size, idx2, result);
		}
		++pos[idx];
		neighborPositions(pos.clone(), size, idx2, result);
		if (++pos[idx] < size[idx]) {
			result.add(pos.clone());
			neighborPositions(pos.clone(), size, idx2, result);
		}
	}
	static char bombChar(int bombs) {
		if (bombs == 0) {return 'o';}
		if (bombs > 26) {return '!';}
		return Character.forDigit(bombs, 36);
	}
	static int[] firstPos(int[] size) {
		return new int[size.length];
	}
	public Minefield(int dimensions, int[] size, int bombcount) {
		this.dimensions = dimensions;
		this.size = size;
		this.bombcount = bombcount;
		orderAssert(dimensions, size);
		int tilecount = 1;
		for (int i = 0; i < dimensions; ++i) {
			tilecount *= size[i];
		}
		this.tilecount = tilecount;
		if (this.bombcount >= this.tilecount) {
			throw new RuntimeException("Too many bombs! You're doomed to fail!");
		}
		this.rand = new Random();
		this.generateTiles();
	}
	/* dynamic proxies of static methods, see above */
	void orderAssert(int[] coordinates) {
		orderAssert(dimensions, coordinates);
	}
	int posToId(int[] pos) {
		return posToId(pos, size);
	}
	/* TODO
	int idToPos(int id) {
		return idToPos(id, size);
	}
	*/
	Tile getTile(int id) {
		return tiles[id];
	}
	Tile getTile(int[] pos) {
		return getTile(posToId(pos));
	}
	int[][] neighborPositions(int[] pos) {
		return neighborPositions(pos, size);
	}
	int[] firstPos() {
		return firstPos(size);
	}
	Tile[] neighborTiles(int[] pos) {
		int[][] neighbors = neighborPositions(pos);
		Tile[] tiles = new Tile[neighbors.length];
		for (int i = 0; i < neighbors.length; ++i) {
			tiles[i] = getTile(neighbors[i]);
		}
		return tiles;
	}
	private int blanktiles;
	void generateTiles() {
		this.tiles = new Tile[tilecount];
		blanktiles = tilecount;
		for (int i = 0; i < bombcount; ++i, --blanktiles) {
			if (blanktiles*5 > tilecount) {
				placeBombGuess();
			} else {
				placeBombTraverse();
			}
		}
		for (int i = 0; i < tilecount; ++i) {
			if (tiles[i] == null) {
				tiles[i] = new SafeTile();
			}
		}
		this.flagcount = 0;
		dirtyBomb();
	}
	void placeBombGuess() {
		//System.out.println("Guessing a tile ID");
		while (true) {
			int tileid = rand.nextInt() % tilecount;
			if (tileid < 0) {tileid = -tileid;}
			Tile tile = this.tiles[tileid];
			boolean exists = tile != null;
			if (exists) { // retry
				//System.out.println(tileid+": Taken");
				continue;
			}
			System.out.println(tileid+": Free!");
			this.tiles[tileid] = new BombTile();
			break;
		}
	}
	void placeBombTraverse() {
		//System.out.println("Traversing tile grid to place bomb");
		int bombtileid = rand.nextInt() % blanktiles;
		int id;
		for (id = 0; id < tilecount; ++id) {
			Tile tile = this.tiles[id];
			if (tile == null) {--bombtileid;}
			if (bombtileid <= 0) {
				this.tiles[id] = new BombTile();
				break;
			}
		}
		if (id == tilecount) {throw new RuntimeException("Didn't place a bomb tile!");}
	}
	int getFlagCount() {return this.flagcount;}
	String renderASCII() {StringBuilder sb = new StringBuilder(); renderASCII(sb); return sb.toString();}
	void renderASCII(StringBuilder out) {int[] pos = new int[dimensions]; renderASCII(out, pos, dimensions);}
	void renderASCII(StringBuilder out, int[] pos, int dim) {
		calcSurroundingBombs();
		int idx;
		switch (dim) {
			case 1:
				idx = dimensions-1;
				int len = size[idx];
				for (int tileid = 0; tileid < len; ++tileid) {
					pos[idx] = tileid;
					Tile tile = getTile(pos);
					char ch = '.';
					if (revealtiles || tile.pressed()) {
						ch = tile.renderASCII();
						if (ch == '.') {
							ch = bombChar(surroundingBombs(pos));
						}
					}
					else if (tile.flagged()) {ch = '/';}
					out.append(ch);
				}
				break;
			case 2:
				idx = dimensions-2;
				for (int y = 0; y < size[0]; ++y) {
					pos[idx] = y;
					renderASCII(out, pos, 1);
					out.append('\n');
				}
				break;
			case 3:
				idx = dimensions-3;
				for (int y = 0; y < size[idx+1]; ++y) {
					pos[idx+1] = y;
					for (int z = 0; z < size[idx]; ++z) {
						if (z > 0) {out.append("   ");}
						pos[idx] = z;
						renderASCII(out, pos, 1);
					}
					out.append('\n');
				}
				break;
			case 4:
				idx = dimensions-4;
				for (int w = 0; w < size[idx]; ++w) {
					pos[idx] = w;
					renderASCII(out, pos, 3);
					out.append('\n');
				}
		}
	}
	int surroundingBombs(int[] pos) {
		return surroundingBombs(posToId(pos));
	}
	/*
	int surroundingBombs(int id) {
		int[] tiles = surroundingTiles(id);
		int bombs = 0;
		for (int i = 0, l = tiles.length; i < l; ++i) {
			if (tiles[id].type == 2) {++bombs;}
		}
		return bombs;
	}
	*/
	int surroundingBombs(int id) {
		return getTile(id).surroundingBombs;
	}
	private boolean dirtyBomb;
	void dirtyBomb() {dirtyBomb = true;}
	void calcSurroundingBombs() {
		if (!dirtyBomb) {return;}
		for (int i = 0; i < tilecount; ++i) {
			getTile(i).surroundingBombs = 0;
		}
		calcSurroundingBombs(0, firstPos());
		dirtyBomb = false;
	}
	void calcSurroundingBombs(int dim, int[] pos) {
		for (int i = 0; i < size[dim]; ++i) {
			pos[dim] = i;
			if (dim == dimensions-1) {
				Tile tile = getTile(pos);
				if (tile.type() == 2) {
					//System.out.println("Careful");
					Tile[] ntiles = neighborTiles(pos);
					for (int j = 0; j < ntiles.length; ++j) {
						++(ntiles[j].surroundingBombs);
					}
				}
			} else {
				calcSurroundingBombs(dim+1, pos);
			}
		}
	}
}
