import java.util.Random;
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
			if (i > 0) {id *= size[i-1];}
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
	static int[] neighborIdOffsets(int[] size) {
		final int dimensions = size.length;
		final int neighbors = pow(3, dimensions)-1;
		//System.out.println(neighbors);
		int[] result = new int[neighbors];
		neighborIdOffsets(size, 0, result, 0, 0);
		return result;
	}
	static int neighborIdOffsets(int[] size, int dimbase, int[] result, int nextresultidx, int base) {
		//System.out.println("> dimbase = "+dimbase+", next idx = "+nextresultidx+", base = "+base);
		final int dimensions = size.length;
		result[nextresultidx++] = base-1;
		if (dimbase > 0) {
			result[nextresultidx++] = base;
		}
		result[nextresultidx++] = base+1;
		final int idx = dimbase+1;
		if (idx < dimensions) {
			final int cursize = size[dimbase];
			nextresultidx = neighborIdOffsets(size, idx, result, nextresultidx, (base-1)*cursize);
			if (dimbase > 0) {
				nextresultidx = neighborIdOffsets(size, idx, result, nextresultidx, base*cursize);
			}
			nextresultidx = neighborIdOffsets(size, idx, result, nextresultidx, (base+1)*cursize);
		}
		//System.out.println("<");
		return nextresultidx;
	}
	static char bombChar(int bombs) {
		if (bombs == 0) {return 'o';}
		if (bombs > 26) {return '!';}
		return Character.forDigit(bombs, 36);
	}
	public Minefield(int dimensions, int[] size, int bombcount) {
		this.dimensions = dimensions;
		this.size = size;
		this.bombcount = bombcount;
		orderAssert(dimensions, size);
		int tilecount = 0;
		for (int i = 0; i < dimensions; ++i) {
			if (i > 0) { tilecount *= size[i-1]; }
			tilecount += size[i];
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
	int[] neighborIdOffsets() {
		return neighborIdOffsets(size);
	}
	Tile getTile(int id) {
		return tiles[id];
	}
	Tile getTile(int[] pos) {
		return getTile(posToId(pos));
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
			boolean exists = false;
			try {
				Tile tile = this.tiles[tileid];
			} catch (NullPointerException e) {
				exists = true;
			}
			if (exists) { // retry
				//System.out.println(tileid+": Taken");
				continue;
			}
			//System.out.println(tileid+": Free!");
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
	Tile[] surroundingTiles(int[] pos) {
		return surroundingTiles(posToId(pos));
	}
	Tile[] surroundingTiles(int id) {
		int[] relNeighbors = neighborIdOffsets();
		int idx = 0;
		for (int i = 0; i < relNeighbors.length; ++i) {
			int nid = relNeighbors[i]+id;
			if (nid < 0 || nid >= tilecount) {continue;}
			idx++;
		}
		Tile[] tiles = new Tile[idx];
		idx = 0;
		for (int i = 0; i < relNeighbors.length; ++i) {
			int nid = relNeighbors[i]+id;
			if (nid < 0 || nid >= tilecount) {continue;}
			tiles[idx++] = getTile(nid);
		}
		return tiles;
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
		for (int i = 0; i < tilecount; ++i) {
			Tile tile = getTile(i);
			if (tile.type() == 2) {
				//System.out.println("Careful");
				Tile[] ntiles = surroundingTiles(i);
				for (int j = 0; j < ntiles.length; ++j) {
					++(ntiles[j].surroundingBombs);
				}
			}
		}
		dirtyBomb = false;
	}
}
