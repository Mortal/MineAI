class WrongOrderException extends IllegalArgumentException {
	private static final long serialVersionUID = -5085755900612388334L;

	WrongOrderException() {
		super("Wrong order in position or size array (array length != dimension count)");
	}
}
